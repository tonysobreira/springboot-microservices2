package com.example.productservice.controller;

import com.example.productservice.entity.Products;
import com.example.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
	@Autowired
	private ProductRepository productRepository;

	@GetMapping
	public List<Products> getAllProducts() {
		return productRepository.findAll();
	}

	@PostMapping
	public Products createProduct(@RequestBody Products product) {
		return productRepository.save(product);
	}
}