
package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.CustomerRequest;
import com.enigma.lastbite.dto.response.CustomerResponse;
import com.enigma.lastbite.entity.Customer;
import com.enigma.lastbite.repository.CustomerRepository;
import com.enigma.lastbite.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequest customerRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId("test-id");
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setMember(true);

        customerRequest = new CustomerRequest();
        customerRequest.setName("Test Customer");
        customerRequest.setEmail("test@example.com");
        customerRequest.setMember(true);
    }

    @Test
    void createCustomer_shouldReturnCustomerResponse_whenSuccessful() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse result = customerService.createCustomer(customerRequest);

        assertNotNull(result);
        assertEquals("Test Customer", result.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomerById_shouldReturnCustomerResponse_whenFound() {
        when(customerRepository.findById("test-id")).thenReturn(Optional.of(customer));

        CustomerResponse result = customerService.getCustomerById("test-id");

        assertNotNull(result);
        assertEquals("Test Customer", result.getName());
    }

    @Test
    void updateCustomer_shouldReturnUpdatedCustomerResponse_whenSuccessful() {
        when(customerRepository.findById("test-id")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerRequest.setName("Updated Customer");
        CustomerResponse result = customerService.updateCustomer("test-id", customerRequest);

        assertNotNull(result);
        assertEquals("Updated Customer", result.getName());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deleteCustomer_shouldDeleteCustomer_whenFound() {
        when(customerRepository.existsById("test-id")).thenReturn(true);
        doNothing().when(customerRepository).deleteById("test-id");

        customerService.deleteCustomer("test-id");

        verify(customerRepository, times(1)).deleteById("test-id");
    }
}
