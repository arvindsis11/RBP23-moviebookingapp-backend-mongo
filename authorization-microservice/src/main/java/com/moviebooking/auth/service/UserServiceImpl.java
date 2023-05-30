package com.moviebooking.auth.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.moviebooking.auth.dto.LoginDto;
import com.moviebooking.auth.dto.ResetDto;
import com.moviebooking.auth.dto.SignupDto;
import com.moviebooking.auth.exception.InvalidInputException;
import com.moviebooking.auth.model.Role;
import com.moviebooking.auth.model.User;
import com.moviebooking.auth.repository.RoleRepository;
import com.moviebooking.auth.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	Map<String, String> mapObj = new HashMap<>();

	// for registration
	@Override
	public ResponseEntity<?> addUser(SignupDto signupDto) {
		if (userRepository.existsByUsername(signupDto.getUsername())
				|| userRepository.existsByEmail(signupDto.getEmail())) {
			mapObj.put("msg", "Username or email is already exists!");
			return new ResponseEntity<>(mapObj, HttpStatus.BAD_REQUEST);
		}
		User user = new User(signupDto.getUsername(), signupDto.getEmail(), signupDto.getPassword(),
				signupDto.getSecurityQuestion(), signupDto.getSecurityAnswer());

		Set<String> strRoles = signupDto.getRoles();
		Set<Role> roles = new HashSet<>();
		if (strRoles == null) {
			Role userRole = roleRepository.findByName("ROLE_CUSTOMER").get();// need to add roles manually into db
			roles.add(userRole);
		} else {
			Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
			roles.add(adminRole);// fix-me
		}

		user.setRoles(roles);
		userRepository.save(user);
		mapObj.put("msg", "User registered successfully");
		return new ResponseEntity<>(mapObj, HttpStatus.OK);
	}

	// for login
	@Override
	public boolean loginUser(LoginDto loginRequest) {

		Optional<User> userObj = userRepository.findByUsername(loginRequest.getUsername());
		if (userObj.isPresent() && userObj.get().getPassword().equals(loginRequest.getPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public ResponseEntity<?> updatePassword(ResetDto resetDto) {// fix--me no need of username
																// here
		if (!userRepository.existsByUsername(resetDto.getUsername())) {
			mapObj.put("msg", "Username doesn't exists!");
			return new ResponseEntity<>(mapObj, HttpStatus.BAD_REQUEST);
		}
		Optional<User> userdata = userRepository.findByUsername(resetDto.getUsername());
		if (resetDto.getSecQuestion().equalsIgnoreCase(userdata.get().getSecurityQuestion())
				&& resetDto.getSecAnswer().equalsIgnoreCase(userdata.get().getSecurityAnswer())) {
			userdata.get().setPassword(resetDto.getNewPassword());
			userRepository.save(userdata.get());
			mapObj.put("msg", "changed password successfully!");
			return new ResponseEntity<>(mapObj, HttpStatus.OK);
		}
		mapObj.put("msg", "could not update password(cause:sec ques not match)!");
		return new ResponseEntity<>(mapObj, HttpStatus.BAD_REQUEST);

	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		// TODO for loading the user from db
		Optional<User> user = userRepository.findByUsername(username);// fix me--handle exceptions here
		if (user.isEmpty()) {
			throw new InvalidInputException("username is not present");
		}
		return user;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

}
