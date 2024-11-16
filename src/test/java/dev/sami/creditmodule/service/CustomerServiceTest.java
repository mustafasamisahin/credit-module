package dev.sami.creditmodule.service;

import dev.sami.creditmodule.dto.CustomerDTO;
import dev.sami.creditmodule.entity.Customer;
import dev.sami.creditmodule.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer createSampleCustomer() {
        return Customer.builder()
                .id(1L)
                .name("Sami")
                .surname("Sahin")
                .creditLimit(BigDecimal.valueOf(10000))
                .usedCreditLimit(BigDecimal.valueOf(2000))
                .build();
    }

    private CustomerDTO createSampleCustomerDTO() {
        return CustomerDTO.builder()
                .id(1L)
                .name("Sami")
                .surname("Sahin")
                .creditLimit(BigDecimal.valueOf(10000))
                .usedCreditLimit(BigDecimal.valueOf(2000))
                .build();
    }

    @Test
    void testCreateCustomer() {
        CustomerDTO inputDTO = createSampleCustomerDTO();
        Customer customer = createSampleCustomer();
        
        when(modelMapper.map(inputDTO, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(inputDTO);

        CustomerDTO result = customerService.createCustomer(inputDTO);

        assertEquals(inputDTO, result);
        verify(customerRepository).save(customer);
    }

    @Test
    void testGetCustomerById() {
        Customer customer = createSampleCustomer();
        CustomerDTO customerDTO = createSampleCustomerDTO();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomerById(1L);

        assertEquals(customerDTO, result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = List.of(createSampleCustomer());
        CustomerDTO customerDTO = createSampleCustomerDTO();

        when(customerRepository.findAll()).thenReturn(customers);
        when(modelMapper.map(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals(customerDTO, result.get(0));
        verify(customerRepository).findAll();
    }

    @Test
    void testUpdateCustomer() {
        Customer existingCustomer = createSampleCustomer();
        CustomerDTO updateDTO = createSampleCustomerDTO();
        updateDTO.setName("Jane");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        when(modelMapper.map(existingCustomer, CustomerDTO.class)).thenReturn(updateDTO);

        CustomerDTO result = customerService.updateCustomer(1L, updateDTO);

        assertEquals(updateDTO, result);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    void testValidateCreditLimit() {
        Customer customer = createSampleCustomer();
        BigDecimal loanAmount = BigDecimal.valueOf(5000);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.validateCreditLimit(1L, loanAmount);

        assertTrue(result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void testValidateCreditLimitExceedsAvailable() {
        Customer customer = createSampleCustomer();
        BigDecimal loanAmount = BigDecimal.valueOf(9000);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.validateCreditLimit(1L, loanAmount);

        assertFalse(result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void testUpdateUsedCreditLimit() {
        Customer customer = createSampleCustomer();
        BigDecimal amount = BigDecimal.valueOf(3000);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        customerService.updateUsedCreditLimit(1L, amount);

        assertEquals(BigDecimal.valueOf(5000), customer.getUsedCreditLimit());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(customer);
    }

    @Test
    void testUpdateUsedCreditLimitNegativeAmount() {
        Customer customer = createSampleCustomer();
        BigDecimal amount = BigDecimal.valueOf(-1000);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        customerService.updateUsedCreditLimit(1L, amount);

        assertEquals(BigDecimal.valueOf(1000), customer.getUsedCreditLimit());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(customer);
    }

    @Test
    void testUpdateUsedCreditLimitExceedsTotalLimit() {
        Customer customer = createSampleCustomer();
        BigDecimal amount = BigDecimal.valueOf(9000);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(IllegalArgumentException.class, () -> 
            customerService.updateUsedCreditLimit(1L, amount)
        );

        verify(customerRepository).findById(1L);
    }
} 