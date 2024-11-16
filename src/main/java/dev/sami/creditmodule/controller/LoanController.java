package dev.sami.creditmodule.controller;

import dev.sami.creditmodule.dto.LoanDTO;
import dev.sami.creditmodule.dto.LoanInstallmentDTO;
import dev.sami.creditmodule.dto.PaymentResultDTO;
import dev.sami.creditmodule.service.LoanService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  public ResponseEntity<LoanDTO> createLoan(
      @RequestParam Long customerId,
      @RequestParam BigDecimal amount,
      @RequestParam Double interestRate,
      @RequestParam Integer numberOfInstallments) {
    LoanDTO loan = loanService.createLoan(customerId, amount, interestRate, numberOfInstallments);
    return ResponseEntity.ok(loan);
  }

  @GetMapping
  public ResponseEntity<List<LoanDTO>> listLoans(@RequestParam Long customerId) {
    List<LoanDTO> loans = loanService.getLoansByCustomer(customerId);
    return ResponseEntity.ok(loans);
  }

  @GetMapping("/{loanId}/installments")
  public ResponseEntity<List<LoanInstallmentDTO>> listInstallments(@PathVariable Long loanId) {
    List<LoanInstallmentDTO> installments = loanService.getInstallmentsByLoan(loanId);
    return ResponseEntity.ok(installments);
  }

  @PostMapping("/{loanId}/pay")
  public ResponseEntity<PaymentResultDTO> payLoan(@PathVariable Long loanId, @RequestParam BigDecimal amount) {
    PaymentResultDTO paymentResultDTO = loanService.payLoan(loanId, amount);
    return ResponseEntity.ok(paymentResultDTO);
  }
}
