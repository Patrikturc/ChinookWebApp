package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.EmployeeDTO;
import com.sparta.pt.chinookwebapp.models.Employee;
import com.sparta.pt.chinookwebapp.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Integer id) {
        Optional<EmployeeDTO> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO createdEmployeeDTO = employeeService.createEmployee(employeeDTO);
            return ResponseEntity.ok(createdEmployeeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeeDTO employeeDTO) {
        Optional<EmployeeDTO> updatedEmployeeDTO = Optional.ofNullable(employeeService.upsertEmployee(id, employeeDTO));
        return updatedEmployeeDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDTO> partialUpdateEmployee(
            @PathVariable Integer id,
            @RequestBody EmployeeDTO employeeDTO) {
        try {
            Optional<EmployeeDTO> updatedEmployeeDTO = employeeService.updateEmployee(id, employeeDTO);
            return updatedEmployeeDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        boolean isDeleted = employeeService.deleteEmployee(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}