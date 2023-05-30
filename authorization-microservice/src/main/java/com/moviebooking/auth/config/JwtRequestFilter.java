package com.moviebooking.auth.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtRequestFilter extends GenericFilterBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Autowired
	private JwtTokenUtil jwtUtils;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// capture the request and validation of token,user here
		HttpServletRequest httpReq = (HttpServletRequest) request;
		LOGGER.debug("filter activated from auth ms");
		try {
			String jwtToken = getJwtToken(httpReq);// extract the token from the request
			if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
				Claims claims = jwtUtils.getJwtClaims(jwtToken);
				httpReq.setAttribute(username, claims);

			}
		} catch (Exception e) {
			LOGGER.error("token string is not valid");
		}

		chain.doFilter(request, response);

	}

	// utility method for getting jwt token here
	private String getJwtToken(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}

}
