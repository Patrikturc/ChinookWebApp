package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.EmployeeDTO;
import com.sparta.pt.chinookwebapp.models.Employee;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class EmployeeDTOConverter extends BaseDTOConverter<Employee, EmployeeDTO> {

    public EmployeeDTO convertToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getTitle(),
                convertInstantToLocalDate(employee.getBirthDate()),
                convertInstantToLocalDate(employee.getHireDate()),
                employee.getAddress(),
                employee.getCity(),
                employee.getState(),
                employee.getCountry(),
                employee.getPostalCode(),
                employee.getPhone(),
                employee.getFax(),
                employee.getEmail(),
                employee.getReportsTo() != null ? employee.getReportsTo().getId() : null,
                employee.getReportsTo() != null ? employee.getReportsTo().getFirstName() + " " + employee.getReportsTo().getLastName() : null
        );
    }

    @Override
    public Employee convertToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setId(employeeDTO.getId());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setTitle(employeeDTO.getTitle());
        employee.setBirthDate(employeeDTO.getBirthDateAsInstant());
        employee.setHireDate(employeeDTO.getHireDateAsInstant());
        employee.setAddress(employeeDTO.getAddress());
        employee.setCity(employeeDTO.getCity());
        employee.setState(employeeDTO.getState());
        employee.setCountry(employeeDTO.getCountry());
        employee.setPostalCode(employeeDTO.getPostalCode());
        employee.setPhone(employeeDTO.getPhone());
        employee.setFax(employeeDTO.getFax());
        employee.setEmail(employeeDTO.getEmail());

        return employee;
    }

    public void updateEmployeeFromDto(Employee existingEmployee, EmployeeDTO employeeDTO) {
        if (employeeDTO.getFirstName() != null) existingEmployee.setFirstName(employeeDTO.getFirstName());
        if (employeeDTO.getLastName() != null) existingEmployee.setLastName(employeeDTO.getLastName());
        if (employeeDTO.getTitle() != null) existingEmployee.setTitle(employeeDTO.getTitle());
        if (employeeDTO.getBirthDateAsInstant() != null) existingEmployee.setBirthDate(employeeDTO.getBirthDateAsInstant());
        if (employeeDTO.getHireDateAsInstant() != null) existingEmployee.setHireDate(employeeDTO.getHireDateAsInstant());
        if (employeeDTO.getAddress() != null) existingEmployee.setAddress(employeeDTO.getAddress());
        if (employeeDTO.getCity() != null) existingEmployee.setCity(employeeDTO.getCity());
        if (employeeDTO.getState() != null) existingEmployee.setState(employeeDTO.getState());
        if (employeeDTO.getCountry() != null) existingEmployee.setCountry(employeeDTO.getCountry());
        if (employeeDTO.getPostalCode() != null) existingEmployee.setPostalCode(employeeDTO.getPostalCode());
        if (employeeDTO.getPhone() != null) existingEmployee.setPhone(employeeDTO.getPhone());
        if (employeeDTO.getFax() != null) existingEmployee.setFax(employeeDTO.getFax());
        if (employeeDTO.getEmail() != null) existingEmployee.setEmail(employeeDTO.getEmail());
    }

    public LocalDate convertInstantToLocalDate(Instant instant) {
        return instant != null ? instant.atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
}