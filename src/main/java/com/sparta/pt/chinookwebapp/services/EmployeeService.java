package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.EmployeeDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.EmployeeDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeDTOConverter employeeDTOConverter;
    private final IdManagementUtils idManagementUtils;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeDTOConverter employeeDTOConverter, IdManagementUtils idManagementUtils) {
        this.employeeRepository = employeeRepository;
        this.employeeDTOConverter = employeeDTOConverter;
        this.idManagementUtils = idManagementUtils;
    }

    public Page<EmployeeDTO> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable).map(employeeDTOConverter::convertToDTO);
    }

    public Optional<EmployeeDTO> getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .map(employeeDTOConverter::convertToDTO);
    }

    public Optional<EmployeeDTO> getEmployeeByName(String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length == 2) {
            String firstName = nameParts[0];
            String lastName = nameParts[1];
            return employeeRepository.findAll().stream()
                    .filter(e -> e.getFirstName().equalsIgnoreCase(firstName) && e.getLastName().equalsIgnoreCase(lastName))
                    .map(employeeDTOConverter::convertToDTO)
                    .findFirst();
        }
        return Optional.empty();
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        try {
            Employee employee = employeeDTOConverter.convertToEntity(employeeDTO);

            List<Employee> allEmployees = employeeRepository.findAll();
            int newId = idManagementUtils.generateId(allEmployees, Employee::getId);
            employee.setId(newId);

            setReportsToByName(employee, employeeDTO.getReportsToFullName());
            Employee createdEmployee = employeeRepository.save(employee);
            return employeeDTOConverter.convertToDTO(createdEmployee);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public Optional<EmployeeDTO> updateEmployee(Integer id, EmployeeDTO employeeDTO) {
        return employeeRepository.findById(id).map(employee -> {
            employeeDTOConverter.updateEmployeeFromDto(employee, employeeDTO);
            setReportsToByName(employee, employeeDTO.getReportsToFullName());
            Employee updatedEmployee = employeeRepository.save(employee);
            return employeeDTOConverter.convertToDTO(updatedEmployee);
        }).map(Optional::of).orElseThrow(() -> new InvalidInputException("Employee not found with id: " + id));
    }

    public EmployeeDTO upsertEmployee(Integer id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id).orElse(new Employee());
        employee.setId(id);
        employeeDTOConverter.updateEmployeeFromDto(employee, employeeDTO);
        setReportsToByName(employee, employeeDTO.getReportsToFullName());

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeDTOConverter.convertToDTO(savedEmployee);
    }

    public boolean deleteEmployee(Integer id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void setReportsToByName(Employee employee, String reportsToFullName) {
        if (reportsToFullName != null && !reportsToFullName.isEmpty()) {
            String[] nameParts = reportsToFullName.split(" ");
            if (nameParts.length == 2) {
                String firstName = nameParts[0];
                String lastName = nameParts[1];
                Optional<Employee> reportsToEmployee = employeeRepository.findAll().stream()
                        .filter(e -> e.getFirstName().equalsIgnoreCase(firstName) && e.getLastName().equalsIgnoreCase(lastName))
                        .findFirst();

                if (reportsToEmployee.isPresent()) {
                    employee.setReportsTo(reportsToEmployee.get());
                } else {
                    throw new IllegalArgumentException("Sorry, that employee doesn't exist.");
                }
            } else {
                throw new IllegalArgumentException("Please provide both first and last name.");
            }
        }
    }
}