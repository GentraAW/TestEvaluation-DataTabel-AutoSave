package com.example.employee.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dto.EmployeeOperationDTO;
import com.example.employee.model.EmployeeModel;
import com.example.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

     @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        List<EmployeeModel> employee = employeeService.getAllEmployee();
        if(employee.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tidak ada data");
        }else{
            return ResponseEntity.ok(employee);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        EmployeeModel employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id + " tidak ditemukan");
        }
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public EmployeeModel createEmployee(@RequestBody EmployeeModel employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeModel employeeDetails) {
        Optional<EmployeeModel> employee = employeeService.updateEmployee(id, employeeDetails);
        try{
            if(employee.isPresent()){
                return ResponseEntity.ok(employee.get());
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id + "Tidak tersedia");
            }
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        boolean isDeleted = employeeService.deleteEmployee(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id + " tidak ditemukan");
        }
        return ResponseEntity.ok("Berhasil dihapus " + id);
    }
    @PostMapping("/bulk")
    public ResponseEntity<List<EmployeeModel>> bulkUpdateEmployees(@RequestBody List<EmployeeOperationDTO> employeeOperations) {
        List<EmployeeModel> updatedEmployees = employeeService.bulkUpdateEmployees(employeeOperations);
        return ResponseEntity.ok(updatedEmployees);
    }
}
