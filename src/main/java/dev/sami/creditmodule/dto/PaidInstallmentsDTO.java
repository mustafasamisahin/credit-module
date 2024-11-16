package dev.sami.creditmodule.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaidInstallmentsDTO {

  private int installmentsPaid;

  private BigDecimal amountSpent;
}