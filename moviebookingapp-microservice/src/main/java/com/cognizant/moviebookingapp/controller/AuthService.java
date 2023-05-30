package com.cognizant.moviebookingapp.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cognizant.moviebookingapp.exception.InvalidInputException;

@Component
public class AuthService {

	@Value("${auth.service.url}")
	private String baseUrl;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

	private RestTemplate restTemplate = new RestTemplate();

	public Map<String, String> validateToken(String token) {
		HttpEntity<String> entity = getHttpEntityWithHeaders(token);
		try {
			ResponseEntity<Map<String, String>> response = restTemplate.exchange(baseUrl + "/validate", HttpMethod.POST,
					entity, new ParameterizedTypeReference<Map<String, String>>() {
					});
			System.err.println(response.getBody());
			if (response.getStatusCode() == HttpStatus.OK) {
				LOGGER.info("token validation success...");
				return response.getBody();
			} else {
				LOGGER.error("invalid token entered");
				throw new InvalidInputException("Failed to validate token: " + response.getStatusCode());
			}
		} catch (Exception e) {
			LOGGER.error("connection refused");
			throw new InvalidInputException(e.getMessage());
		}
	}

	private HttpEntity<String> getHttpEntityWithHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		return new HttpEntity<>("", headers);
	}
}
