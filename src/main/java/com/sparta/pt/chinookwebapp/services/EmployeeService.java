package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.EmployeeDTO;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employee -> new EmployeeDTO(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getTitle(),
                        employee.getBirthDate() != null ? employee.getBirthDate().toString() : null,
                        employee.getHireDate() != null ? employee.getHireDate().toString() : null,
                        employee.getAddress(),
                        employee.getCity(),
                        employee.getState(),
                        employee.getCountry(),
                        employee.getPostalCode(),
                        employee.getPhone(),
                        employee.getFax(),
                        employee.getEmail()))
                .collect(Collectors.toList());
    }

    public Optional<EmployeeDTO> getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .map(employee -> new EmployeeDTO(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getTitle(),
                        employee.getBirthDate() != null ? employee.getBirthDate().toString() : null,
                        employee.getHireDate() != null ? employee.getHireDate().toString() : null,
                        employee.getAddress(),
                        employee.getCity(),
                        employee.getState(),
                        employee.getCountry(),
                        employee.getPostalCode(),
                        employee.getPhone(),
                        employee.getFax(),
                        employee.getEmail()));
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> updateEmployee(Integer id, Employee employeeDetails) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setFirstName(employeeDetails.getFirstName());
                    employee.setLastName(employeeDetails.getLastName());
                    employee.setTitle(employeeDetails.getTitle());
                    employee.setBirthDate(employeeDetails.getBirthDate());
                    employee.setHireDate(employeeDetails.getHireDate());
                    employee.setAddress(employeeDetails.getAddress());
                    employee.setCity(employeeDetails.getCity());
                    employee.setState(employeeDetails.getState());
                    employee.setCountry(employeeDetails.getCountry());
                    employee.setPostalCode(employeeDetails.getPostalCode());
                    employee.setPhone(employeeDetails.getPhone());
                    employee.setFax(employeeDetails.getFax());
                    employee.setEmail(employeeDetails.getEmail());
                    return employeeRepository.save(employee);
                });
    }

    public boolean deleteEmployee(Integer id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}