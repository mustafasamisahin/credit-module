package dev.sami.creditmodule.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class LoanInstallmentDTO {

  private Long id;

  private Long loanId;

  @NotNull
  @Positive
  private BigDecimal amount;

  private BigDecimal paidAmount;

  private LocalDate dueDate;

  private LocalDate paymentDate;

  private Boolean isPaid;
} 