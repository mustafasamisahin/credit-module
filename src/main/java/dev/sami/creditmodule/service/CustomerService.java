package dev.sami.creditmodule.service;

import dev.sami.creditmodule.dto.CustomerDTO;
import dev.sami.creditmodule.entity.Customer;
import dev.sami.creditmodule.repository.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  private final ModelMapper modelMapper;

  public CustomerDTO createCustomer(CustomerDTO customerDTO) {
    Customer customer = modelMapper.map(customerDTO, Customer.class);
    Customer createdCustomer = customerRepository.save(customer);
    return modelMapper.map(createdCustomer, CustomerDTO.class);
  }

  public CustomerDTO getCustomerById(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new RuntimeException("Customer not found"));
    return modelMapper.map(customer, CustomerDTO.class);
  }

  public List<CustomerDTO> getAllCustomers() {
    List<Customer> customers = customerRepository.findAll();
    return customers.stream()
                    .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                    .toList();
  }

  public CustomerDTO updateCustomer(Long customerId, CustomerDTO updatedCustomerDTO) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new RuntimeException("Customer not found"));
    customer.setName(updatedCustomerDTO.getName());
    customer.setSurname(updatedCustomerDTO.getSurname());
    customer.setCreditLimit(updatedCustomerDTO.getCreditLimit());
    Customer updatedCustomer = customerRepository.save(customer);
    return modelMapper.map(updatedCustomer, CustomerDTO.class);
  }

  public void deleteCustomer(Long customerId) {
    customerRepository.deleteById(customerId);
  }

  public boolean validateCreditLimit(Long customerId, BigDecimal loanAmount) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new RuntimeException("Customer not found"));
    BigDecimal availableCredit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
    return availableCredit.compareTo(loanAmount) >= 0;
  }

  public void updateUsedCreditLimit(Long customerId, BigDecimal amount) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new RuntimeException("Customer not found"));
    BigDecimal newCreditLimit = customer.getUsedCreditLimit().add(amount);
    if (newCreditLimit.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Credit limit cannot be negative");
    }
    if (newCreditLimit.compareTo(customer.getCreditLimit()) > 0) {
      throw new IllegalArgumentException("Used credit limit cannot exceed total credit limit");
    }
    customer.setUsedCreditLimit(newCreditLimit);
    customerRepository.save(customer);
  }
}
