package com.jwt.employeeService;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jwt.config.JWTFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	
	private static final Logger log=LoggerFactory.getLogger(JWTService.class);
	 private String secretkey = "";

//	private final String SECRET_KEY = Base64.getEncoder().encodeToString(
//	        Jwts.SIG.HS256.key().build().getEncoded()
//	);
	public JWTService() {
		try {
			KeyGenerator generator=KeyGenerator.getInstance("HmacSHA256");
			//random key
			SecretKey key = generator.generateKey();
			//encoded into string
			secretkey = Base64.getEncoder().encodeToString(key.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public String generateToken(String email, Map<String, Object> claims) {
		return Jwts.builder() 
				   .claims(claims)
				   .subject(email)
				   .issuedAt(new Date(System.currentTimeMillis()))  //issued time with mile seconds
				   .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
				   .signWith(getKey())
				   .compact();
	}

	private SecretKey getKey() {
		byte[] bytes = Decoders.BASE64.decode(secretkey);
		return Keys.hmacShaKeyFor(bytes);
	}
	


	public String extractUsername(String token) {
		String name = extractClaims(token,Claims::getSubject);
		System.out.println(name);
		return name;
	}

	

	private <T>T extractClaims(String token, Function<Claims, T> claims) {
		final Claims allClaims = extractAllClaims(token);
		return claims.apply(allClaims);
		
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				   .verifyWith(getKey())
				   .build()
				   .parseSignedClaims(token)
				   .getPayload();	
	}

	public boolean validateToken(String token, UserDetails userByUsername) {
		String username = extractUsername(token);
		log.info(userByUsername.getUsername()+" "+userByUsername.getPassword());
		return (username.equals(userByUsername.getUsername()) &&! isTokenExpaired(token));
	}

	private boolean isTokenExpaired(String token) {
		return extractExpairation(token).before(new Date());
	}

	private Date extractExpairation(String token) {
		return extractClaims(token,Claims::getExpiration);
	}
	
	
	
}
