package com.frontend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.frontend.services.ApiService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import model.Product;

@Controller
@RequestMapping("/page")
public class ApiController {

	private static final String TOKEN_COOKIE_NAME = "token";

	@Autowired
	private ApiService apiService;

	/**
	 * Show the Add Product form
	 */

	@GetMapping("/add-product")
	public String showAddProductForm(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("operation", "Add New Product");
		return "addNewProduct";
	}

	/**
	 * Save or update a product
	 */
	@PostMapping("/page/product/save")
	public String saveProduct() {

		return "";
	}

	@PostMapping("/product/save")
	public String saveOrUpdateProduct(@ModelAttribute Product product, @RequestParam("photo") MultipartFile image,
			HttpServletRequest request) {
		String token = extractTokenFromCookies(request);

		this.apiService.saveAPI(product, token, image);

		return "redirect:/product/list";
	}

	/**
	 * Get product by ID and show in form for editing
	 */
	@GetMapping("/product/{id}")
	public String getProductById(@PathVariable int id, Model model, HttpServletRequest request) {
		String token = extractTokenFromCookies(request);
		Product product = apiService.getProductById(id, token);
		model.addAttribute("product", product);
		model.addAttribute("operation", "Update Product");
		return "addNewProduct";
	}

	/**
	 * List all products
	 */
	@GetMapping("/products")
	public String listAllProducts(Model model, HttpServletRequest request) {
		String token = extractTokenFromCookies(request);
		List<Product> products = apiService.getAllProducts(token);
		model.addAttribute("products", products);
		model.addAttribute("operation", "List Products");
		return "products";
	}

//	Search by name or letters
	@GetMapping("/product/search")
	public String searchAllProductsByName(@RequestParam("query") String query, Model model,
			HttpServletRequest request) {
		String token = extractTokenFromCookies(request);
		List<Product> products = apiService.getSearchedProducts(query, token);
		model.addAttribute("products", products);
		model.addAttribute("operation", "List Products");
		System.out.println("ApiController.searchAllProductsByName(): " + query);
		return "products";
	}

	/**
	 * Delete product by ID
	 */
	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable int id, HttpServletRequest request) {
		String token = extractTokenFromCookies(request);
		apiService.deleteProductById(id, token);
		return "redirect:/page/products";
	}

	/**
	 * Extract JWT token from cookies
	 */
	private String extractTokenFromCookies(HttpServletRequest request) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}
}
