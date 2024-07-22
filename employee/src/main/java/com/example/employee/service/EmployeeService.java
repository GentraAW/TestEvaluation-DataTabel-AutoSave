package com.example.employee.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employee.dto.EmployeeOperationDTO;
import com.example.employee.model.EmployeeModel;
import com.example.employee.repository.EmployeeRepository;

import jakarta.transaction.Transactional;



@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmployeeModel> getAllEmployee(){
        return employeeRepository.findAll();
    }

    public EmployeeModel getEmployeeById(Long id){
        return employeeRepository.findById(id).orElse(null);
    }

    public EmployeeModel createEmployee(EmployeeModel employeeModel){
        return employeeRepository.save(employeeModel);
    }

    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<EmployeeModel> updateEmployee(Long id, EmployeeModel employeeModel){
        Optional<EmployeeModel> existingEmployee = employeeRepository.findById(id);

        if(existingEmployee.isPresent()){
            EmployeeModel employee = existingEmployee.get();
            if(employeeModel.getName() != null){
                employee.setName(employeeModel.getName());
            }
            if(employeeModel.getAge() != null){
                employee.setAge(employeeModel.getAge());
            }
            if(employeeModel.getEmail() != null){
                employee.setEmail(employeeModel.getEmail());
            }
            if(employeeModel.getPhone() != null){    
                employee.setPhone(employeeModel.getPhone());

            }
            return Optional.of(employeeRepository.save(employee));
        }
        return Optional.empty();
    }

    @Transactional
    public List<EmployeeModel> bulkUpdateEmployees(List<EmployeeOperationDTO> employeeOperations) {
        List<EmployeeModel> updatedEmployees = new ArrayList<>();

        for (EmployeeOperationDTO operation : employeeOperations) {
            switch (operation.getOperation().toLowerCase()) {
                case "create":
                    EmployeeModel newEmployee = new EmployeeModel();
                    newEmployee.setName(operation.getName());
                    newEmployee.setAge(operation.getAge());
                    newEmployee.setEmail(operation.getEmail());
                    newEmployee.setPhone(operation.getPhone());
                    updatedEmployees.add(employeeRepository.save(newEmployee));
                    break;

                case "update":
                    if (operation.getId() != null) {
                        Optional<EmployeeModel> existingEmployeeOpt = employeeRepository.findById(operation.getId());
                        if (existingEmployeeOpt.isPresent()) {
                            EmployeeModel existingEmployee = existingEmployeeOpt.get();
                            if (operation.getName() != null) {
                                existingEmployee.setName(operation.getName());
                            }
                            if (operation.getAge() != null) {
                                existingEmployee.setAge(operation.getAge());
                            }
                            if (operation.getEmail() != null) {
                                existingEmployee.setEmail(operation.getEmail());
                            }
                            if (operation.getPhone() != null) {
                                existingEmployee.setPhone(operation.getPhone());
                            }
                            updatedEmployees.add(employeeRepository.save(existingEmployee));
                        }
                    }
                    break;

                case "delete":
                    if (operation.getId() != null) {
                        deleteEmployee(operation.getId());
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Operation tidak diketahui: " + operation.getOperation());
            }
        }

        return updatedEmployees;
    }

}
