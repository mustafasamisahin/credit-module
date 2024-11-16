package dev.sami.creditmodule.controller;

import dev.sami.creditmodule.dto.CustomerDTO;
import dev.sami.creditmodule.service.CustomerService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping
  public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
    CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
    return ResponseEntity.ok(createdCustomer);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
    CustomerDTO customer = customerService.getCustomerById(id);
    return ResponseEntity.ok(customer);
  }

  @GetMapping
  public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
    List<CustomerDTO> customers = customerService.getAllCustomers();
    return ResponseEntity.ok(customers);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id,
                                                    @RequestBody CustomerDTO updatedCustomerDTO) {
    CustomerDTO customer = customerService.updateCustomer(id, updatedCustomerDTO);
    return ResponseEntity.ok(customer);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }
}
