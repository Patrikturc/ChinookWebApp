package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.CustomerDTO;
import com.sparta.pt.chinookwebapp.models.Customer;
import com.sparta.pt.chinookwebapp.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDTOConverter extends BaseDTOConverter<Customer, CustomerDTO> {
    private final EmployeeService employeeService;
    private final EmployeeDTOConverter employeeDTOConverter;

    @Autowired
    public CustomerDTOConverter(EmployeeService employeeService, EmployeeDTOConverter employeeDTOConverter) {
        this.employeeService = employeeService;
        this.employeeDTOConverter = employeeDTOConverter;
    }

    public CustomerDTO convertToDTO(Customer customer) {
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

    public Customer convertToEntity(CustomerDTO dto) {
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
        setSupportRepByName(customer, dto.getSupportRepName());
        return customer;
    }

    public void setSupportRepByName(Customer customer, String supportRepName) {
        if (supportRepName != null && !supportRepName.isEmpty()) {
            employeeService.getEmployeeByName(supportRepName)
                    .map(employeeDTOConverter::convertToEntity)
                    .ifPresent(customer::setSupportRep);
        }
    }
}
