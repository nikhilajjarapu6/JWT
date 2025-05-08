package com.jwt.employeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//@Service
//custom own behavior
public class CustomAuthProvider implements AuthenticationProvider {

	@Autowired
	private CustomUserDetailes userDetailes;
	
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = authentication.getName();
		String rawPassword = authentication.getCredentials().toString();
		
		UserDetails userByUsername = userDetailes.loadUserByUsername(username);
		//manually verify password
		if(!encoder.matches(rawPassword, userByUsername.getPassword())) {
			throw new BadCredentialsException("Invalid user detailes");
		}
		
		//return UsernamePasswordAuthenticationToken object for dao provider for verification
		return new UsernamePasswordAuthenticationToken(userByUsername,rawPassword,userByUsername.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		 return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
