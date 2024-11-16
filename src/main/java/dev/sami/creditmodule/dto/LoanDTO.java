package dev.sami.creditmodule.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class LoanDTO {

  private Long id;

  private Long customerId;

  private BigDecimal loanAmount;

  private Integer numberOfInstallments;

  private Boolean isPaid;

  private String createDate;
}
