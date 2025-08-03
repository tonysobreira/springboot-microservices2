package com.example.userservice.controller;

import com.example.userservice.entity.Users;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public List<Users> getAllUsers() {
		return userRepository.findAll();
	}
}