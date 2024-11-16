package dev.sami.creditmodule.controller;

import dev.sami.creditmodule.dto.LoanDTO;
import dev.sami.creditmodule.dto.LoanInstallmentDTO;
import dev.sami.creditmodule.dto.PaymentResultDTO;
import dev.sami.creditmodule.service.LoanService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

  @Mock
  private LoanService loanService;

  @InjectMocks
  private LoanController loanController;

  @Test
  void testCreateLoan() {
    Long customerId = 1L;
    BigDecimal amount = BigDecimal.valueOf(1000);
    Double interestRate = 0.2;
    int numberOfInstallments = 12;

    LoanDTO loanDTO = new LoanDTO();
    when(loanService.createLoan(customerId, amount, interestRate, numberOfInstallments))
        .thenReturn(loanDTO);

    ResponseEntity<LoanDTO> response =
        loanController.createLoan(customerId, amount, interestRate, numberOfInstallments);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(loanDTO, response.getBody());
    verify(loanService).createLoan(customerId, amount, interestRate, numberOfInstallments);
  }

  @Test
  void testListLoans() {
    Long customerId = 1L;
    List<LoanDTO> loans = List.of(new LoanDTO());
    when(loanService.getLoansByCustomer(customerId)).thenReturn(loans);

    ResponseEntity<List<LoanDTO>> response = loanController.listLoans(customerId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(loans, response.getBody());
    verify(loanService).getLoansByCustomer(customerId);
  }

  @Test
  void testListInstallments() {
    Long loanId = 1L;
    List<LoanInstallmentDTO> installments = List.of(new LoanInstallmentDTO());
    when(loanService.getInstallmentsByLoan(loanId)).thenReturn(installments);

    ResponseEntity<List<LoanInstallmentDTO>> response = loanController.listInstallments(loanId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(installments, response.getBody());
    verify(loanService).getInstallmentsByLoan(loanId);
  }

  @Test
  void testPayLoan() {
    Long loanId = 1L;
    BigDecimal amount = BigDecimal.valueOf(500);
    PaymentResultDTO paymentResultDTO = new PaymentResultDTO(2, BigDecimal.valueOf(500), false);
    when(loanService.payLoan(loanId, amount)).thenReturn(paymentResultDTO);

    ResponseEntity<PaymentResultDTO> response = loanController.payLoan(loanId, amount);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(paymentResultDTO, response.getBody());
    verify(loanService).payLoan(loanId, amount);
  }
}
