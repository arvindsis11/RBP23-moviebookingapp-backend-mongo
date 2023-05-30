package com.moviebooking.auth.dto;

import java.util.Set;

import com.moviebooking.auth.model.Role;

public class JwtResponse {
	private String token;
	private String id;
	private String username;
	private String email;
	private Set<Role> roles;

	public JwtResponse(String accessToken, String id, String username, String email, Set<Role> set) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = set;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Role> getRoles() {
		return roles;
	}
}
