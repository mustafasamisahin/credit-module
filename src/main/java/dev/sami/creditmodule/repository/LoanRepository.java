package dev.sami.creditmodule.repository;

import dev.sami.creditmodule.entity.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

  List<Loan> findByCustomerId(Long customerId);
}
