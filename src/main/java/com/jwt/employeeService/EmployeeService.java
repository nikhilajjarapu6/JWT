package com.jwt.employeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwt.dto.Login;
import com.jwt.employeeRepository.EmployeeRepository;
import com.jwt.entity.Employee;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private AuthenticationManager  manager;
	
	@Autowired
	private JWTService jwtService;
	
	private static final Logger log=LoggerFactory.getLogger(EmployeeService.class);
	
	public Employee save(Employee emp) {
		String encode = encoder.encode(emp.getPassword());
		emp.setPassword(encode);
		return repository.save(emp);
	}
	
	public Optional<Employee> getEmp(int id) {
		return repository.findById(id);
	}

	public String verify(Login login) {
		 Optional<Employee> employeeOpt = repository.findByEmail(login.getEmail());
		    if (employeeOpt.isEmpty()) {
		        return "Invalid email or password.";
		    }
		  Authentication authentication=manager.authenticate(
				  		new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
		  log.info("authenticated object is "+SecurityContextHolder.getContext().getAuthentication());
		  if(authentication.isAuthenticated()) {
			  Map<String, Object> claims= new HashMap<>();
			  claims.put("ROLE", "EMP");
			  return jwtService.generateToken(login.getEmail(),claims);
		  }
	        return "Invalid email or password.";
	}

	public List<Employee> findAll() {
		return repository.findAll();
	}
}
