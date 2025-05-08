package com.jwt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.dto.Login;
import com.jwt.employeeService.EmployeeService;
import com.jwt.entity.Employee;

@RestController
@RequestMapping("/emp")
public class EmployeeController {
	
	@Autowired
	private EmployeeService service;
	
	@PostMapping("/save")
	public ResponseEntity<Employee> save(@RequestBody Employee employee){
		Employee save = service.save(employee);
		return ResponseEntity.status(HttpStatus.CREATED).body(save);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<Employee> save(@PathVariable int id){
		Employee save = service.getEmp(id).get();
		return ResponseEntity.ok(save);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Login login){
		String verify = service.verify(login);
		return ResponseEntity.ok(verify);
	}
	
	@GetMapping("/get")
	public ResponseEntity<List<Employee>> getAll(){
		List<Employee> save = service.findAll();
		return ResponseEntity.ok(save);
	}
	
	
	
}
