package com.jwt.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jwt.employeeService.CustomUserDetailes;
import com.jwt.employeeService.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {
	
 	@Autowired
    private JWTService jwtService;

 	
 	@Autowired
 	private CustomUserDetailes userDetailes;
 	
 	@Autowired
 	private ApplicationContext context;
 	
 	private static final Logger log=LoggerFactory.getLogger(JWTFilter.class);
 	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	
		String authHeader = request.getHeader("Authorization");
		log.info("header "+authHeader);
		String token="";
		String username="";
		
		
		 if (authHeader != null && authHeader.startsWith("Bearer ")) {
		        token = authHeader.substring(7);
		        log.info("Extracted Token: " + token);

		        // Extract username from token
		        username = jwtService.extractUsername(token);
		        log.info("Extracted Username: " + username);

		        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		            UserDetails userByUsername = userDetailes.loadUserByUsername(username);

		            if (jwtService.validateToken(token, userByUsername)) {
		                UsernamePasswordAuthenticationToken authToken =
		                        new UsernamePasswordAuthenticationToken(userByUsername, null, userByUsername.getAuthorities());
		                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		                SecurityContextHolder.getContext().setAuthentication(authToken);

		                log.debug("Authentication successful");
		            }
		        }
		    }
		
		
		
		filterChain.doFilter(request, response);
	}
	
	

}
