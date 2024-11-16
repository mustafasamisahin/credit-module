package dev.sami.creditmodule.service;

import dev.sami.creditmodule.dto.LoanDTO;
import dev.sami.creditmodule.dto.LoanInstallmentDTO;
import dev.sami.creditmodule.dto.PaidInstallmentsDTO;
import dev.sami.creditmodule.dto.PaymentResultDTO;
import dev.sami.creditmodule.entity.Loan;
import dev.sami.creditmodule.entity.LoanInstallment;
import dev.sami.creditmodule.repository.LoanRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

  @Mock
  private LoanRepository loanRepository;

  @Mock
  private LoanInstallmentService loanInstallmentService;

  @Mock
  private CustomerService customerService;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private LoanService loanService;

  private Loan createSampleLoan() {
    return Loan.builder()
               .id(1L)
               .customerId(1L)
               .loanAmount(BigDecimal.valueOf(1200.0))
               .numberOfInstallments(6)
               .isPaid(false)
               .build();
  }

  private LoanInstallment createSampleInstallment(Long loanId, int monthOffset) {
    return LoanInstallment.builder()
                          .loanId(loanId)
                          .amount(BigDecimal.valueOf(200.0))
                          .paidAmount(BigDecimal.ZERO)
                          .isPaid(false)
                          .dueDate(LocalDate.now().plusMonths(monthOffset).withDayOfMonth(1))
                          .build();
  }

  @Test
  void testCreateLoan() {
    Long customerId = 1L;
    BigDecimal amount = BigDecimal.valueOf(120);
    double interestRate = 0.2;
    int numberOfInstallments = 6;

    Loan loan = new Loan();
    loan.setId(1L);
    loan.setCustomerId(customerId);
    loan.setLoanAmount(amount.multiply(BigDecimal.valueOf(1 + interestRate)));
    loan.setNumberOfInstallments(numberOfInstallments);
    loan.setIsPaid(false);

    LoanDTO loanDTO = new LoanDTO();

    when(customerService.validateCreditLimit(customerId, BigDecimal.valueOf(144.0))).thenReturn(true);
    when(loanRepository.save(any(Loan.class))).thenReturn(loan);
    when(modelMapper.map(loan, LoanDTO.class)).thenReturn(loanDTO);

    LoanDTO result = loanService.createLoan(customerId, amount, interestRate, numberOfInstallments);

    assertEquals(loanDTO, result);
    verify(customerService).validateCreditLimit(customerId, BigDecimal.valueOf(144.0));
    verify(loanRepository).save(any(Loan.class));
    verify(loanInstallmentService, times(numberOfInstallments))
        .createInstallment(any(LoanInstallment.class));
  }

  @Test
  void testGetLoansByCustomer() {
    Long customerId = 1L;
    List<Loan> loans = List.of(new Loan());
    List<LoanDTO> loanDTOs = List.of(new LoanDTO());

    when(loanRepository.findByCustomerId(customerId)).thenReturn(loans);
    when(modelMapper.map(any(Loan.class), eq(LoanDTO.class)))
        .thenReturn(new LoanDTO());

    List<LoanDTO> result = loanService.getLoansByCustomer(customerId);

    assertEquals(loanDTOs.size(), result.size());
    verify(loanRepository).findByCustomerId(customerId);
  }

  @Test
  void testGetInstallmentsByLoan() {
    Long loanId = 1L;
    List<LoanInstallment> installments = List.of(new LoanInstallment());
    List<LoanInstallmentDTO> installmentDTOs = List.of(new LoanInstallmentDTO());

    when(loanInstallmentService.getInstallmentsByLoan(loanId)).thenReturn(installments);
    when(modelMapper.map(any(LoanInstallment.class), eq(LoanInstallmentDTO.class)))
        .thenReturn(new LoanInstallmentDTO());

    List<LoanInstallmentDTO> result = loanService.getInstallmentsByLoan(loanId);

    assertEquals(installmentDTOs.size(), result.size());
    verify(loanInstallmentService).getInstallmentsByLoan(loanId);
  }

  @Test
  void testPayLoan() {
    Loan loan = createSampleLoan();
    LoanInstallment installment = createSampleInstallment(loan.getId(), 1);
    BigDecimal paymentAmount = BigDecimal.valueOf(500);

    List<LoanInstallment> unpaidInstallments = List.of(installment);
    PaidInstallmentsDTO paymentResponse = new PaidInstallmentsDTO(1, paymentAmount);

    when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
    when(loanInstallmentService.getPendingInstallments(loan.getId())).thenReturn(unpaidInstallments);
    when(loanInstallmentService.payInstallments(anyList(), eq(paymentAmount)))
        .thenReturn(paymentResponse);

    PaymentResultDTO result = loanService.payLoan(loan.getId(), paymentAmount);

    assertEquals(paymentResponse.getAmountSpent(), result.getTotalAmountPaid());
    verify(loanRepository).findById(loan.getId());
    verify(loanInstallmentService).payInstallments(anyList(), eq(paymentAmount));
  }
}
