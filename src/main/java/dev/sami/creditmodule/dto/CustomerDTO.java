package dev.sami.creditmodule.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class CustomerDTO {

  private Long id;

  private String name;

  private String surname;

  private BigDecimal creditLimit;

  private BigDecimal usedCreditLimit;
}
