package com.moviebooking.auth.config;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.moviebooking.auth.dto.LoginDto;
import com.moviebooking.auth.exception.InvalidInputException;
import com.moviebooking.auth.model.Role;
import com.moviebooking.auth.model.User;
import com.moviebooking.auth.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

	@Value("${auth.app.jwtSecret}")
	private String jwtSecret;

	@Value("${auth.app.jwtExpireTime}")
	private int jwtExpireTime;

	@Autowired
	UserService userService;

	public String generatToken(LoginDto loginRequest) {
		try {
			String username = loginRequest.getUsername();
			String password = loginRequest.getPassword();
			if (username == null || password == null) {
				LOGGER.warn("please enter valid credentials");
				throw new InvalidInputException("please enter valid credentials");
			}
			boolean flag = userService.loginUser(loginRequest);// validating the user here only
			Optional<User> user = userService.getUserByUsername(username);// for getting the role from user to iterate
			List<String> roleNames = user.get().getRoles().stream().map(Role::getName).collect(Collectors.toList());
			LOGGER.info("this role is logging in " + roleNames.get(0) + " " + user.get().getUsername());
			if (!flag) {
				LOGGER.error("entered credentials are invalid");
				throw new InvalidInputException("Invalid credentials");
			}
			LOGGER.info("Generating jwt token here...");
			String jwtToken = Jwts.builder().setSubject(username).claim("role", roleNames.get(0).toString())
					.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpireTime))
					.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
			return jwtToken;
		} catch (InvalidInputException e) {
			LOGGER.error("unable to generate token");
			throw new InvalidInputException(e.getMessage());
		}
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public Claims getJwtClaims(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		return claims;
	}

	// will need this later
	public String getRoleFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

		String role = (String) claims.get("role");

		return role;
	}

	public boolean validateJwtToken(String token) {// fix me
		String authToken = null;
		String user = null;
		if (token != null && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				user = getUserNameFromJwtToken(authToken);
				return true;
			} catch (Exception e) {
				LOGGER.warn("token not starts with Bearer");
				throw new InvalidInputException("invalid token");
			}
		}
		return false;
	}

}
