package dev.sami.creditmodule.repository;

import dev.sami.creditmodule.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
