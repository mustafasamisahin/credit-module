package dev.sami.creditmodule.service;

import dev.sami.creditmodule.dto.PaidInstallmentsDTO;
import dev.sami.creditmodule.entity.LoanInstallment;
import dev.sami.creditmodule.repository.LoanInstallmentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanInstallmentService {

  private final LoanInstallmentRepository installmentRepository;

  public void createInstallment(LoanInstallment installment) {
    installmentRepository.save(installment);
  }

  public List<LoanInstallment> getInstallmentsByLoan(Long loanId) {
    return installmentRepository.findByLoanIdOrderByDueDate(loanId);
  }

  public List<LoanInstallment> getPendingInstallments(Long loanId) {
    return installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDate(loanId);
  }

  public PaidInstallmentsDTO payInstallments(List<LoanInstallment> pendingInstallments, BigDecimal amount) {
    BigDecimal remainingAmount = amount;
    int paidInstallmentCount = 0;
    for (LoanInstallment installment : pendingInstallments) {
      if (remainingAmount.compareTo(installment.getAmount()) >= 0) {
        BigDecimal installmentAmount = installment.getAmount();
        installment.setPaidAmount(installmentAmount);
        installment.setPaymentDate(LocalDate.now());
        installment.setIsPaid(true);
        remainingAmount = remainingAmount.subtract(installmentAmount);
        installmentRepository.save(installment);
        paidInstallmentCount++;
      } else {
        break;
      }
    }
    return new PaidInstallmentsDTO(paidInstallmentCount, amount.subtract(remainingAmount));
  }
}
