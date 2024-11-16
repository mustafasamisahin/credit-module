package dev.sami.creditmodule.service;

import dev.sami.creditmodule.dto.LoanDTO;
import dev.sami.creditmodule.dto.LoanInstallmentDTO;
import dev.sami.creditmodule.dto.PaidInstallmentsDTO;
import dev.sami.creditmodule.dto.PaymentResultDTO;
import dev.sami.creditmodule.entity.Loan;
import dev.sami.creditmodule.entity.LoanInstallment;
import dev.sami.creditmodule.repository.LoanRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

  private final LoanRepository loanRepository;

  private final LoanInstallmentService loanInstallmentService;

  private final CustomerService customerService;

  private final ModelMapper modelMapper;

  public LoanDTO createLoan(Long customerId, BigDecimal amount, Double interestRate, int numberOfInstallments) {
    if (interestRate < 0.1 || interestRate > 0.5) {
      throw new IllegalArgumentException("Interest rate must be between 0.1 and 0.5");
    }
    if (!List.of(6, 9, 12, 24).contains(numberOfInstallments)) {
      throw new IllegalArgumentException("Number of installments must be 6, 9, 12, or 24");
    }

    BigDecimal totalAmount = amount.multiply(BigDecimal.valueOf(1 + interestRate));
    if (!customerService.validateCreditLimit(customerId, totalAmount)) {
      throw new IllegalArgumentException("Customer does not have enough credit limit");
    }

    Loan loan = Loan.builder()
                    .customerId(customerId)
                    .loanAmount(totalAmount.setScale(2, RoundingMode.HALF_UP))
                    .numberOfInstallments(numberOfInstallments)
                    .isPaid(false)
                    .createDate(LocalDate.now())
                    .build();

    loan = loanRepository.save(loan);

    for (int i = 1; i <= numberOfInstallments; i++) {
      LoanInstallment installment = LoanInstallment.builder()
                                                   .loanId(loan.getId())
                                                   .amount(totalAmount.divide(BigDecimal.valueOf(numberOfInstallments),
                                                                              RoundingMode.HALF_UP))
                                                   .paidAmount(BigDecimal.ZERO)
                                                   .isPaid(false)
                                                   .dueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1))
                                                   .build();
      loanInstallmentService.createInstallment(installment);
    }

    customerService.updateUsedCreditLimit(customerId, totalAmount);
    return modelMapper.map(loan, LoanDTO.class);
  }

  public List<LoanDTO> getLoansByCustomer(Long customerId) {
    List<Loan> loans = loanRepository.findByCustomerId(customerId);
    return loans.stream()
                .map(loan -> modelMapper.map(loan, LoanDTO.class))
                .toList();
  }

  public List<LoanInstallmentDTO> getInstallmentsByLoan(Long loanId) {
    List<LoanInstallment> installments = loanInstallmentService.getInstallmentsByLoan(loanId);
    return installments.stream()
                       .map(installment -> modelMapper.map(installment, LoanInstallmentDTO.class))
                       .toList();
  }

  public PaymentResultDTO payLoan(Long loanId, BigDecimal amount) {
    Loan loan = loanRepository.findById(loanId)
                              .orElseThrow(() -> new RuntimeException("Loan not found"));

    List<LoanInstallment> unpaidInstallments = loanInstallmentService.getPendingInstallments(loanId);

    List<LoanInstallment> pendingInstallments =
        unpaidInstallments.stream()
                          .filter(installment -> installment.getDueDate().isBefore(LocalDate.now().plusMonths(3)))
                          .toList();

    PaidInstallmentsDTO paymentResponse = loanInstallmentService.payInstallments(pendingInstallments, amount);

    boolean loanFullyPaid = unpaidInstallments.size() == paymentResponse.getInstallmentsPaid();
    if (loanFullyPaid) {
      loan.setIsPaid(true);
      loanRepository.save(loan);
    }

    customerService.updateUsedCreditLimit(loan.getCustomerId(), paymentResponse.getAmountSpent().negate());
    return new PaymentResultDTO(paymentResponse.getInstallmentsPaid(), paymentResponse.getAmountSpent(), loanFullyPaid);
  }
}
