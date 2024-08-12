package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.CustomerRepository;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDTO> getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
            Customer customer = convertToEntity(customerDTO);
            List<Customer> allCustomers = customerRepository.findAll();
            int maxId = allCustomers.stream()
                    .max(Comparator.comparingInt(Customer::getId))
                    .map(Customer::getId)
                    .orElse(0);

            customer.setId(maxId + 1);

            try{
                setSupportRepByName(customer, customerDTO.getSupportRepName());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException(e.getMessage());
            }
            Customer savedCustomer = customerRepository.save(customer);
            return convertToDTO(savedCustomer);
    }

    public Optional<CustomerDTO> updateCustomer(Integer id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    Customer updatedCustomer = convertToEntity(customerDTO);
                    updatedCustomer.setId(id);

                    try{
                        setSupportRepByName(updatedCustomer, customerDTO.getSupportRepName());
                    } catch (IllegalArgumentException e) {
                        throw new InvalidInputException(e.getMessage());
                    }
                    Customer savedCustomer = customerRepository.save(updatedCustomer);
                    return convertToDTO(savedCustomer);
                });
    }

    public CustomerDTO upsertCustomer(Integer id, CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        setSupportRepByName(customer, customerDTO.getSupportRepName());

        if (!customerRepository.existsById(id)) {
            customer = convertToEntity(customerDTO);
            customer.setId(id);
            try{
                setSupportRepByName(customer, customerDTO.getSupportRepName());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException(e.getMessage());
            }
            Customer savedCustomer = customerRepository.save(customer);
            return convertToDTO(savedCustomer);
        }
        customer.setId(id);
        updateCustomer(id, customerDTO);

        return convertToDTO(customer);
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
                if (supportRep.isPresent()) {
                    customer.setSupportRep(supportRep.get());
                } else {
                    throw new IllegalArgumentException("Sorry, that employee doesn't exist.");
                }
            } else {
                throw new IllegalArgumentException("Please provide both first and last name.");
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

    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setCompany(dto.getCompany());
        customer.setAddress(dto.getAddress());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setCountry(dto.getCountry());
        customer.setPostalCode(dto.getPostalCode());
        customer.setPhone(dto.getPhone());
        customer.setFax(dto.getFax());
        customer.setEmail(dto.getEmail());
        return customer;
    }

    private CustomerDTO convertToDTO(Customer customer) {
        return new CustomerDTO(
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
                customer.getSupportRep() != null ? customer.getSupportRep().getFirstName() + " " + customer.getSupportRep().getLastName() : null
        );
    }
}