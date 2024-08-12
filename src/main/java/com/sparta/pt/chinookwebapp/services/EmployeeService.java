package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.EmployeeDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

import java.time.Instant;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EmployeeDTO> getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        try {
            Employee employee = convertToEntity(employeeDTO);
            List<Employee> allEmployees = employeeRepository.findAll();
            int maxId = allEmployees.stream()
                    .max(Comparator.comparingInt(Employee::getId))
                    .map(Employee::getId)
                    .orElse(0);

            employee.setId(maxId + 1);

            setReportsToByName(employee, employeeDTO.getReportsToFullName());

            Employee createdEmployee = employeeRepository.save(employee);
            return convertToDTO(createdEmployee);

        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public Optional<EmployeeDTO> updateEmployee(Integer id, EmployeeDTO employeeDTO) {
        try {
            return employeeRepository.findById(id)
                    .map(employee -> {
                        updateEntityFromDTO(employee, employeeDTO);

                        setReportsToByName(employee, employeeDTO.getReportsToFullName());

                        Employee updatedEmployee = employeeRepository.save(employee);
                        return convertToDTO(updatedEmployee);
                    });
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public EmployeeDTO upsertEmployee(Integer id, EmployeeDTO employeeDTO) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        Employee employee;

        if (existingEmployee.isPresent()) {
            employee = existingEmployee.get();
            updateEntityFromDTO(employee, employeeDTO);
        } else {
            employee = convertToEntity(employeeDTO);
            employee.setId(id);
        }

        try {
            setReportsToByName(employee, employeeDTO.getReportsToFullName());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
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

    private void setReportsToById(Employee employee, Integer reportsToId) {
        if (reportsToId != null) {
            employeeRepository.findById(reportsToId).ifPresent(employee::setReportsTo);
        }
    }

    public boolean deleteEmployee(Integer id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private EmployeeDTO convertToDTO(Employee employee) {
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

    private Employee convertToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        setEmployeeWithDto(employee, employeeDTO);

        if (employeeDTO.getReportsToId() != null) {
            Optional<Employee> reportsToEmployee = employeeRepository.findById(employeeDTO.getReportsToId());
            reportsToEmployee.ifPresent(employee::setReportsTo);
        }

        return employee;
    }

    private void updateEntityFromDTO(Employee employee, EmployeeDTO employeeDTO) {
        setEmployeeWithDto(employee, employeeDTO);

        setReportsToById(employee, employeeDTO.getReportsToId());
    }

    private void setEmployeeWithDto(Employee employee, EmployeeDTO employeeDTO) {
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
    }

    public LocalDate convertInstantToLocalDate(Instant instant) {
        return instant != null ? instant.atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
}