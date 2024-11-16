package dev.sami.creditmodule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {

  @Id
  @GeneratedValue
  private Long id;

  private Long loanId;

  private BigDecimal amount;

  private BigDecimal paidAmount;

  private LocalDate dueDate;

  private LocalDate paymentDate;

  private Boolean isPaid;
}
