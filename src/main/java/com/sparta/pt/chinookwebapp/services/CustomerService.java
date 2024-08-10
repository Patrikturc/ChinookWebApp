package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.CustomerRepository;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Comparator;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
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

    public Customer createCustomer(Customer customer, String supportRepName) {
        setSupportRepByName(customer, supportRepName);

        List<Customer> allCustomers = customerRepository.findAll();
        int maxId = allCustomers.stream()
                .max(Comparator.comparingInt(Customer::getId))
                .map(Customer::getId)
                .orElse(0);

        customer.setId(maxId + 1);

        return customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(Integer id, Customer customerDetails, String supportRepName) {
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

                    // Set the support rep
                    setSupportRepByName(customer, supportRepName);

                    return customerRepository.save(customer);
                });
    }

    private void setSupportRepByName(Customer customer, String supportRepName) {
        if (supportRepName != null && !supportRepName.isEmpty()) {
            String[] nameParts = supportRepName.split(" ");
            if (nameParts.length == 2) {
                String firstName = nameParts[0];
                String lastName = nameParts[1];
                Optional<Employee> supportRep = employeeRepository.findAll().stream()
                        .filter(e -> e.getFirstName().equalsIgnoreCase(firstName) && e.getLastName().equalsIgnoreCase(lastName))
                        .findFirst();

                supportRep.ifPresent(customer::setSupportRep);
            }
        }
    }

    public boolean deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}