package com.example.userservice.controller;

import com.example.userservice.entity.Users;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String jwt = jwtUtil.generateToken(userDetails);

		Map<String, String> response = new HashMap<>();
		response.put("token", jwt);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	public ResponseEntity<Users> register(@RequestBody Users user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return ResponseEntity.ok(userRepository.save(user));
	}
}