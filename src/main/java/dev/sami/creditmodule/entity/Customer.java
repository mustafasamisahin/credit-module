package dev.sami.creditmodule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private String surname;

  private BigDecimal creditLimit;

  private BigDecimal usedCreditLimit;
}
