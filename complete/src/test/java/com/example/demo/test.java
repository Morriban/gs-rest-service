package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeManager employeeManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void resetData() {
        // Clear out existing list and re-add initial sample data
        Employees base = new Employees();
        base.getEmployeeList().add(new Employee(1, "Prem", "Tiwari", "prem@gmail.com"));
        base.getEmployeeList().add(new Employee(2, "Vikash", "Kumar", "vikash@gmail.com"));
        base.getEmployeeList().add(new Employee(3, "Ritesh", "Ojha", "ritesh@gmail.com"));
        // Use reflection or setter to inject fresh Employees into manager
        employeeManager.getAllEmployees().setEmployeeList(base.getEmployeeList());
    }

    @Test
    public void testGetEmployees() throws Exception {
        mockMvc.perform(get("/employees/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeList").isArray())
                .andExpect(jsonPath("$.employeeList.length()").value(3))
                .andExpect(jsonPath("$.employeeList[0].firstName").value("Prem"))
                .andExpect(jsonPath("$.employeeList[1].email").value("vikash@gmail.com"));
    }

    @Test
    public void testAddEmployee() throws Exception {
        Employee newEmp = new Employee(null, "Alice", "Smith", "alice@example.com");

        mockMvc.perform(post("/employees/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmp)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/employees/4")));

        // Verify new size is 4
        mockMvc.perform(get("/employees/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeList.length()").value(4))
                .andExpect(jsonPath("$.employeeList[3].firstName").value("Alice"))
                .andExpect(jsonPath("$.employeeList[3].id").value(4));
    }
}
