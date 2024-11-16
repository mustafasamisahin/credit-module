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
public class Loan {

  @Id
  @GeneratedValue
  private Long id;

  private Long customerId;

  private BigDecimal loanAmount;

  private Integer numberOfInstallments;

  private LocalDate createDate;

  private Boolean isPaid;
}
