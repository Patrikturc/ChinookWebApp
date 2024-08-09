package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Integer id) {
        Optional<CustomerDTO> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            Customer customer = new Customer();
            customer.setFirstName(customerDTO.getFirstName());
            customer.setLastName(customerDTO.getLastName());
            customer.setCompany(customerDTO.getCompany());
            customer.setAddress(customerDTO.getAddress());
            customer.setCity(customerDTO.getCity());
            customer.setState(customerDTO.getState());
            customer.setCountry(customerDTO.getCountry());
            customer.setPostalCode(customerDTO.getPostalCode());
            customer.setPhone(customerDTO.getPhone());
            customer.setFax(customerDTO.getFax());
            customer.setEmail(customerDTO.getEmail());

            Customer createdCustomer = customerService.createCustomer(customer, customerDTO.getSupportRepName());

            return ResponseEntity.ok(new CustomerDTO(
                    createdCustomer.getId(),
                    createdCustomer.getFirstName(),
                    createdCustomer.getLastName(),
                    createdCustomer.getCompany(),
                    createdCustomer.getAddress(),
                    createdCustomer.getCity(),
                    createdCustomer.getState(),
                    createdCustomer.getCountry(),
                    createdCustomer.getPostalCode(),
                    createdCustomer.getPhone(),
                    createdCustomer.getFax(),
                    createdCustomer.getEmail(),
                    createdCustomer.getSupportRep() != null ? createdCustomer.getSupportRep().getFirstName() + " " + createdCustomer.getSupportRep().getLastName() : null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Integer id, @RequestBody CustomerDTO customerDTO) {
        try {
            Customer customerDetails = new Customer();
            customerDetails.setFirstName(customerDTO.getFirstName());
            customerDetails.setLastName(customerDTO.getLastName());
            customerDetails.setCompany(customerDTO.getCompany());
            customerDetails.setAddress(customerDTO.getAddress());
            customerDetails.setCity(customerDTO.getCity());
            customerDetails.setState(customerDTO.getState());
            customerDetails.setCountry(customerDTO.getCountry());
            customerDetails.setPostalCode(customerDTO.getPostalCode());
            customerDetails.setPhone(customerDTO.getPhone());
            customerDetails.setFax(customerDTO.getFax());
            customerDetails.setEmail(customerDTO.getEmail());

            Optional<Customer> updatedCustomer = customerService.updateCustomer(id, customerDetails, customerDTO.getSupportRepName());

            return updatedCustomer.map(c -> new CustomerDTO(
                            c.getId(),
                            c.getFirstName(),
                            c.getLastName(),
                            c.getCompany(),
                            c.getAddress(),
                            c.getCity(),
                            c.getState(),
                            c.getCountry(),
                            c.getPostalCode(),
                            c.getPhone(),
                            c.getFax(),
                            c.getEmail(),
                            c.getSupportRep() != null ? c.getSupportRep().getFirstName() + " " + c.getSupportRep().getLastName() : null
                    ))
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        boolean isDeleted = customerService.deleteCustomer(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}