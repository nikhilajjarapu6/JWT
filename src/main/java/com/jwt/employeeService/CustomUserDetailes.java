package com.jwt.employeeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwt.employeeRepository.EmployeeRepository;
import com.jwt.entity.Employee;

@Service
public class CustomUserDetailes implements UserDetailsService {

	@Autowired
	public EmployeeRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee emp = repository.findByEmail(username)
				  .orElseThrow(()->new UsernameNotFoundException("user not found:"+username));
		return new User(
				emp.getEmail(),
				emp.getPassword(),
				List.of(new SimpleGrantedAuthority("ROLE EMP"))
				);
	}

}
