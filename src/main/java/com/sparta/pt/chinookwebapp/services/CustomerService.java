package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> new CustomerDTO(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getCompany(),
                        customer.getAddress(),
                        customer.getCity(),
                        customer.getState(),
                        customer.getCountry(),
                        customer.getPostalCode(),
                        customer.getPhone(),
                        customer.getFax(),
                        customer.getEmail(),
                        customer.getSupportRep() != null ? customer.getSupportRep().getFirstName() + " " + customer.getSupportRep().getLastName() : null))
                .collect(Collectors.toList());
    }

    public Optional<CustomerDTO> getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .map(customer -> new CustomerDTO(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getCompany(),
                        customer.getAddress(),
                        customer.getCity(),
                        customer.getState(),
                        customer.getCountry(),
                        customer.getPostalCode(),
                        customer.getPhone(),
                        customer.getFax(),
                        customer.getEmail(),
                        customer.getSupportRep() != null ? customer.getSupportRep().getFirstName() + " " + customer.getSupportRep().getLastName() : null));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(Integer id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setFirstName(customerDetails.getFirstName());
                    customer.setLastName(customerDetails.getLastName());
                    customer.setCompany(customerDetails.getCompany());
                    customer.setAddress(customerDetails.getAddress());
                    customer.setCity(customerDetails.getCity());
                    customer.setState(customerDetails.getState());
                    customer.setCountry(customerDetails.getCountry());
                    customer.setPostalCode(customerDetails.getPostalCode());
                    customer.setPhone(customerDetails.getPhone());
                    customer.setFax(customerDetails.getFax());
                    customer.setEmail(customerDetails.getEmail());
                    customer.setSupportRep(customerDetails.getSupportRep());
                    return customerRepository.save(customer);
                });
    }

    public boolean deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}