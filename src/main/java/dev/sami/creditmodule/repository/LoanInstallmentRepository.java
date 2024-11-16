package dev.sami.creditmodule.repository;

import dev.sami.creditmodule.entity.LoanInstallment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

  List<LoanInstallment> findByLoanIdAndIsPaidFalseOrderByDueDate(Long loanId);

  List<LoanInstallment> findByLoanIdOrderByDueDate(Long loanId);
}
