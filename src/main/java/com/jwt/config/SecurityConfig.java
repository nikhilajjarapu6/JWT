package com.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwt.employeeService.CustomUserDetailes;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomUserDetailes userDetailes; 
	
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	private JWTFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf->csrf.disable())
				.authorizeHttpRequests(auth->auth
					.requestMatchers("/emp/login","/emp/save").permitAll()
					.anyRequest().authenticated()
					)
//				.userDetailsService(userDetailes) no need because setting this on provider manually
//				.formLogin(Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
		//setting provider with our password encoder and user details
		//verifications internally
		//to our own flow of provider then create separate custom provider and register here 
		@Bean
		public AuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
			authenticationProvider.setUserDetailsService(userDetailes);
			authenticationProvider.setPasswordEncoder(passwordEncoder());
			return authenticationProvider;
		}
		
		@Bean
		public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
			return configuration.getAuthenticationManager();
		}
		
	
		
		//registering provider and manager of our own
//		 @Bean
//		    public AuthenticationProvider authenticationProvider(CustomAuthProvider customAuthProvider) {
//		        return customAuthProvider;  // Use our custom provider
//		    }
//
//		    @Bean
//		    public AuthenticationManager authenticationManager(CustomAuthProvider customAuthProvider) {
//		        return new ProviderManager(List.of(customAuthProvider));  // Use custom provider
//		    }
//	
}
