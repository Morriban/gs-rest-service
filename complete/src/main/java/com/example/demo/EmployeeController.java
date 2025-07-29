package com.example.demo;

import com.example.demo.Employee;
import com.example.demo.EmployeeManager;
import com.example.demo.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// REST Controller for managing employees
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeManager employeeManager;

    // GET endpoint to fetch all employees
    @GetMapping("/")
    public Employees getEmployees() {
        return employeeManager.getAllEmployees();
    }

    // POST endpoint to add a new employee
    @PostMapping("/")
    public ResponseEntity<Object>
        addEmployee(@RequestBody Employee employee) {

            // Generate ID for the new employee
            Integer id = employeeManager.getAllEmployees()
                    .getEmployeeList().size() + 1;
            employee.setId(id);

            // Add employee to the list
        employeeManager.addEmployee(employee);

            // Build location URI for the new employee
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(employee.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
    }
}
