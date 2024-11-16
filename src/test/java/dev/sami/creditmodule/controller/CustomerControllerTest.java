package dev.sami.creditmodule.controller;

import dev.sami.creditmodule.dto.CustomerDTO;
import dev.sami.creditmodule.entity.Customer;
import dev.sami.creditmodule.service.CustomerService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

  @Mock
  private CustomerService customerService;

  @InjectMocks
  private CustomerController customerController;

  private final CustomerDTO sampleCustomerDTO = createSampleCustomerDTO();

  private CustomerDTO createSampleCustomerDTO() {
    return CustomerDTO.builder()
                                   .id(1L)
                                   .name("Sami")
                                   .surname("Sahin")
                                   .creditLimit(BigDecimal.valueOf(1000.00))
                                   .usedCreditLimit(BigDecimal.ZERO)
                                   .build();
  }

  @Test
  void createCustomer_ShouldReturnCreatedCustomer() {
    when(customerService.createCustomer(any(CustomerDTO.class)))
        .thenReturn(sampleCustomerDTO);

    ResponseEntity<CustomerDTO> response = customerController.createCustomer(sampleCustomerDTO);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(sampleCustomerDTO, response.getBody());
    verify(customerService).createCustomer(any(CustomerDTO.class));
  }

  @Test
  void getCustomerById_ShouldReturnCustomer() {
    when(customerService.getCustomerById(1L)).thenReturn(sampleCustomerDTO);

    ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(sampleCustomerDTO, response.getBody());
    verify(customerService).getCustomerById(1L);
  }

  @Test
  void getAllCustomers_ShouldReturnListOfCustomers() {
    List<CustomerDTO> customerList = Collections.singletonList(sampleCustomerDTO);
    when(customerService.getAllCustomers()).thenReturn(customerList);

    ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(customerList, response.getBody());
    verify(customerService).getAllCustomers();
  }

  @Test
  void updateCustomer_ShouldReturnUpdatedCustomer() {
    CustomerDTO updatedCustomerDTO = sampleCustomerDTO;
    updatedCustomerDTO.setCreditLimit(BigDecimal.valueOf(2000.00));
    when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class)))
        .thenReturn(updatedCustomerDTO);

    ResponseEntity<CustomerDTO> response = customerController.updateCustomer(1L, updatedCustomerDTO);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(updatedCustomerDTO, response.getBody());
    verify(customerService).updateCustomer(eq(1L), any(CustomerDTO.class));
  }

  @Test
  void deleteCustomer_ShouldReturnNoContent() {
    doNothing().when(customerService).deleteCustomer(1L);

    ResponseEntity<Void> response = customerController.deleteCustomer(1L);

    assertNotNull(response);
    assertEquals(204, response.getStatusCode().value());
    assertNull(response.getBody());
    verify(customerService).deleteCustomer(1L);
  }
} 