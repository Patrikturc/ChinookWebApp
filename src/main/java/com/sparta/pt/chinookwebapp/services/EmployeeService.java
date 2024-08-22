package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.EmployeeController;
import com.sparta.pt.chinookwebapp.dtos.EmployeeDTO;
import com.sparta.pt.chinookwebapp.exceptions.InvalidInputException;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.repositories.EmployeeRepository;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class EmployeeService extends BaseService<Employee, EmployeeDTO, EmployeeRepository> {

    private final EmployeeRepository employeeRepository;
    private final HateoasUtils<EmployeeDTO> hateoasUtils;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           HateoasUtils<EmployeeDTO> hateoasUtils,
                           WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(employeeRepository, hateoasUtils, linkBuilderFactory);
        this.employeeRepository = employeeRepository;
        this.hateoasUtils = hateoasUtils;
    }

    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getAll(pageable, this::toDto, EmployeeController.class, EmployeeDTO::getId);
    }

    public ResponseEntity<EntityModel<EmployeeDTO>> getEmployeeById(Integer id) {
        return getById(id, this::toDto, EmployeeController.class, EmployeeDTO::getId);
    }

    public ResponseEntity<EntityModel<EmployeeDTO>> createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        setReportsToByName(employee, employeeDTO.getReportsToFullName());
        return create(employee, this::toDto, EmployeeController.class, EmployeeDTO::getId);
    }

    public ResponseEntity<EntityModel<EmployeeDTO>> updateEmployee(Integer id, EmployeeDTO employeeDTO) {
        Employee updatedEmployee = convertToEntity(employeeDTO);
        setReportsToByName(updatedEmployee, employeeDTO.getReportsToFullName());
        return update(id, updatedEmployee, this::toDto, EmployeeController.class, EmployeeDTO::getId);
    }

    public ResponseEntity<PagedModel<EntityModel<EmployeeDTO>>> getEmployeesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable);
        Page<EmployeeDTO> dtoPage = employees.map(this::toDto);

        return hateoasUtils.createPagedResponse(
                dtoPage,
                EmployeeController.class,
                EmployeeDTO::getId
        );
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
                reportsToEmployee.ifPresent(employee::setReportsTo);
            } else {
                throw new InvalidInputException("Please provide both first and last name.");
            }
        }
    }

    @Override
    protected void updateEntity(Employee existingEmployee, Employee employeeDetails) {
        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        existingEmployee.setTitle(employeeDetails.getTitle());
        existingEmployee.setBirthDate(employeeDetails.getBirthDate());
        existingEmployee.setHireDate(employeeDetails.getHireDate());
        existingEmployee.setAddress(employeeDetails.getAddress());
        existingEmployee.setCity(employeeDetails.getCity());
        existingEmployee.setState(employeeDetails.getState());
        existingEmployee.setCountry(employeeDetails.getCountry());
        existingEmployee.setPostalCode(employeeDetails.getPostalCode());
        existingEmployee.setPhone(employeeDetails.getPhone());
        existingEmployee.setFax(employeeDetails.getFax());
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setReportsTo(employeeDetails.getReportsTo());
    }

    @Override
    protected void updateEntityPartial(Employee existingEntity, EmployeeDTO dtoDetails) {
        if (dtoDetails.getFirstName() != null) existingEntity.setFirstName(dtoDetails.getFirstName());
        if (dtoDetails.getLastName() != null) existingEntity.setLastName(dtoDetails.getLastName());
        if (dtoDetails.getTitle() != null) existingEntity.setTitle(dtoDetails.getTitle());
        if (dtoDetails.getBirthDate() != null) existingEntity.setBirthDate(dtoDetails.getBirthDateAsInstant());
        if (dtoDetails.getHireDate() != null) existingEntity.setHireDate(dtoDetails.getHireDateAsInstant());
        if (dtoDetails.getAddress() != null) existingEntity.setAddress(dtoDetails.getAddress());
        if (dtoDetails.getCity() != null) existingEntity.setCity(dtoDetails.getCity());
        if (dtoDetails.getState() != null) existingEntity.setState(dtoDetails.getState());
        if (dtoDetails.getCountry() != null) existingEntity.setCountry(dtoDetails.getCountry());
        if (dtoDetails.getPostalCode() != null) existingEntity.setPostalCode(dtoDetails.getPostalCode());
        if (dtoDetails.getPhone() != null) existingEntity.setPhone(dtoDetails.getPhone());
        if (dtoDetails.getFax() != null) existingEntity.setFax(dtoDetails.getFax());
        if (dtoDetails.getEmail() != null) existingEntity.setEmail(dtoDetails.getEmail());
        if (dtoDetails.getReportsToFullName() != null) setReportsToByName(existingEntity, dtoDetails.getReportsToFullName());
    }

    private EmployeeDTO toDto(Employee employee) {
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