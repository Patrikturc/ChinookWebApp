package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.CustomerDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.repositories.CustomerRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final IdManagementUtils idManagementUtils;
    private final CustomerDTOConverter customerDTOConverter;
    private final EmployeeService employeeService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, IdManagementUtils idManagementUtils, CustomerDTOConverter customerDTOConverter, EmployeeService employeeService) {
        this.customerRepository = customerRepository;
        this.idManagementUtils = idManagementUtils;
        this.customerDTOConverter = customerDTOConverter;
        this.employeeService = employeeService;
    }

    public Page<CustomerDTO> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable).map(customerDTOConverter::convertToDTO);
    }

    public Optional<CustomerDTO> getCustomerById(Integer id) {
        return customerRepository.findById(id).map(customerDTOConverter::convertToDTO);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
            Customer customer = customerDTOConverter.convertToEntity(customerDTO);
            List<Customer> allCustomers = customerRepository.findAll();
            int newId = idManagementUtils.generateId(allCustomers, Customer::getId);
            customer.setId(newId);

            try{
                employeeService.setEmployeeByName(customer.getSupportRep(), customerDTO.getSupportRepName());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException(e.getMessage());
            }
            Customer savedCustomer = customerRepository.save(customer);
            return customerDTOConverter.convertToDTO(savedCustomer);
    }

    public Optional<CustomerDTO> updateCustomer(Integer id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    Customer updatedCustomer = customerDTOConverter.convertToEntity(customerDTO);
                    updatedCustomer.setId(id);

                    try{
                        employeeService.setEmployeeByName(updatedCustomer.getSupportRep(), customerDTO.getSupportRepName());
                    } catch (IllegalArgumentException e) {
                        throw new InvalidInputException(e.getMessage());
                    }
                    Customer savedCustomer = customerRepository.save(updatedCustomer);
                    return customerDTOConverter.convertToDTO(savedCustomer);
                });
    }

    public CustomerDTO upsertCustomer(Integer id, CustomerDTO customerDTO) {
        Customer customer = customerDTOConverter.convertToEntity(customerDTO);
        employeeService.setEmployeeByName(customer.getSupportRep(), customerDTO.getSupportRepName());

        if (!customerRepository.existsById(id)) {
            customer = customerDTOConverter.convertToEntity(customerDTO);
            customer.setId(id);
            try{
                employeeService.setEmployeeByName(customer.getSupportRep(), customerDTO.getSupportRepName());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException(e.getMessage());
            }
            Customer savedCustomer = customerRepository.save(customer);
            return customerDTOConverter.convertToDTO(savedCustomer);
        }
        customer.setId(id);
        updateCustomer(id, customerDTO);

        return customerDTOConverter.convertToDTO(customer);
    }

    public boolean deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}