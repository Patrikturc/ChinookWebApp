package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.CustomerController;
import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.CustomerRepository;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class CustomerService extends BaseService<Customer, CustomerDTO, CustomerRepository> {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           EmployeeRepository employeeRepository,
                           HateoasUtils<CustomerDTO> hateoasUtils,
                           WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(customerRepository, hateoasUtils, linkBuilderFactory);
        this.employeeRepository = employeeRepository;
    }

    public ResponseEntity<PagedModel<EntityModel<CustomerDTO>>> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getAll(pageable, this::convertToDTO, CustomerController.class, CustomerDTO::getId);
    }

    public ResponseEntity<EntityModel<CustomerDTO>> getCustomerById(Integer id) {
        return getById(id, this::convertToDTO, CustomerController.class, CustomerDTO::getId);
    }

    public ResponseEntity<EntityModel<CustomerDTO>> createCustomer(CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        setSupportRepByName(customer, customerDTO.getSupportRepName());
        return create(customer, this::convertToDTO, CustomerController.class, CustomerDTO::getId);
    }

    public ResponseEntity<EntityModel<CustomerDTO>> updateCustomer(Integer id, CustomerDTO customerDTO) {
        Customer updatedCustomer = convertToEntity(customerDTO);
        setSupportRepByName(updatedCustomer, customerDTO.getSupportRepName());
        return update(id, updatedCustomer, this::convertToDTO, CustomerController.class, CustomerDTO::getId);
    }

    private void setSupportRepByName(Customer customer, String supportRepName) {
        if (supportRepName != null && !supportRepName.isEmpty()) {
            String[] nameParts = supportRepName.split(" ");
            if (nameParts.length == 2) {
                String firstName = nameParts[0];
                String lastName = nameParts[1];
                Optional<Employee> supportRep = employeeRepository.findAll().stream()
                        .filter(emp -> emp.getFirstName().equals(firstName) && emp.getLastName().equals(lastName))
                        .findFirst();
                supportRep.ifPresent(customer::setSupportRep);
            } else {
                throw new InvalidInputException("Invalid support rep name: " + supportRepName);
            }
        }
    }

    private Customer convertToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setCountry(customerDTO.getCountry());
        customer.setPostalCode(customerDTO.getPostalCode());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
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

    @Override
    protected void updateEntity(Customer existingCustomer, Customer customerDetails) {
        existingCustomer.setFirstName(customerDetails.getFirstName());
        existingCustomer.setLastName(customerDetails.getLastName());
        existingCustomer.setCountry(customerDetails.getCountry());
        existingCustomer.setPostalCode(customerDetails.getPostalCode());
        existingCustomer.setPhone(customerDetails.getPhone());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setSupportRep(customerDetails.getSupportRep());
    }

    @Override
    protected void updateEntityPartial(Customer existingEntity, CustomerDTO dtoDetails) {
        if (dtoDetails.getFirstName() != null) existingEntity.setFirstName(dtoDetails.getFirstName());
        if (dtoDetails.getLastName() != null) existingEntity.setLastName(dtoDetails.getLastName());
        if (dtoDetails.getCountry() != null) existingEntity.setCountry(dtoDetails.getCountry());
        if (dtoDetails.getPostalCode() != null) existingEntity.setPostalCode(dtoDetails.getPostalCode());
        if (dtoDetails.getPhone() != null) existingEntity.setPhone(dtoDetails.getPhone());
        if (dtoDetails.getEmail() != null) existingEntity.setEmail(dtoDetails.getEmail());
        if (dtoDetails.getSupportRepName() != null) setSupportRepByName(existingEntity, dtoDetails.getSupportRepName());
    }
}
