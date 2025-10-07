package com.frontend.services;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Product;

@Service
public class ApiService {

	private static final String BASE_URL = "http://localhost:8080/api";

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Creates a new product or updates an existing one based on its ID.
	 */
//	public String saveAPI(Product product, String token, MultipartFile file) {
//		HttpEntity<Product> entity = buildEntityWithBody(product, token);
//
//		if (product.getId() == 0) {
//			// Create new product
//			restTemplate.postForEntity(BASE_URL + "/product", entity, Product.class);
//		} else {
//			// Update existing product
//			restTemplate.exchange(BASE_URL + "/product/" + product.getId(), HttpMethod.PUT, entity, Void.class);
//		}
//		System.out.println("Data from client : " + product);
//		System.out.println("Image from client : " + file);
//
//		return "redirect:/page/products";
//	}

	public String saveAPI(Product product, String token, MultipartFile file) {
	    try {
	        // Convert Product object to JSON string
	        ObjectMapper objectMapper = new ObjectMapper();
	        String productJson = objectMapper.writeValueAsString(product);

	        // Headers for the overall request
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	        headers.setBearerAuth(token); // Include Authorization header

	        // Create the multipart body
	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

	        // JSON part (product)
	        HttpHeaders jsonHeaders = new HttpHeaders();
	        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<String> productPart = new HttpEntity<>(productJson, jsonHeaders);
	        body.add("product", productPart);

	        // File part (optional)
	        if (file != null && !file.isEmpty()) {
	            HttpHeaders fileHeaders = new HttpHeaders();
	            fileHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));

	            
	            body.add("image", file.getBytes());
	        }

	        // Final request
	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

	        // Make the POST request
	        ResponseEntity<Product> response = restTemplate.postForEntity(
	                BASE_URL + "/product", requestEntity, Product.class);

	        System.out.println("Saved product: " + response.getBody());

	        // ✅ Return something (like a redirect URL or status)
	        return "redirect:/page/products";

	    } catch (Exception e) {
	        e.printStackTrace();
	        // Optionally log the error or return error page
	        return "error"; // Or handle better
	    }
	}

	/**
	 * Retrieves the list of all products.
	 */
	public List<Product> getAllProducts(String token) {
		HttpEntity<Void> entity = buildEntity(token);

		ResponseEntity<List<Product>> response = restTemplate.exchange(BASE_URL + "/products", HttpMethod.GET, entity,
				new ParameterizedTypeReference<>() {
				});

		return response.getBody();
	}

//    Retrieves the list of the all products using name
	public List<Product> getSearchedProducts(String param, String token) {
		HttpEntity<Void> entity = buildEntity(token);

		// Append param as query parameter ?query=value
		String url = BASE_URL + "/products/search?query=" + UriUtils.encode(param, StandardCharsets.UTF_8);

		ResponseEntity<List<Product>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				new ParameterizedTypeReference<>() {
				});

		return response.getBody();
	}

	/**
	 * Retrieves a product by its ID.
	 */
	public Product getProductById(int id, String token) {
		HttpEntity<Void> entity = buildEntity(token);

		ResponseEntity<Product> response = restTemplate.exchange(BASE_URL + "/product/" + id, HttpMethod.GET, entity,
				Product.class);

		return response.getBody();
	}

	/**
	 * Deletes a product by its ID.
	 */
	public void deleteProductById(int id, String token) {
		HttpEntity<Void> entity = buildEntity(token);

		restTemplate.exchange(BASE_URL + "/product/" + id, HttpMethod.DELETE, entity, Void.class);
	}

	/**
	 * Creates HttpHeaders with JWT Bearer token.
	 */
	private HttpHeaders createAuthHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * Builds an HttpEntity with a request body and authorization headers.
	 */
	private <T> HttpEntity<T> buildEntityWithBody(T body, String token) {
		return new HttpEntity<>(body, createAuthHeaders(token));
	}

	/**
	 * Builds an HttpEntity with only authorization headers (no body).
	 */
	private HttpEntity<Void> buildEntity(String token) {
		return new HttpEntity<>(createAuthHeaders(token));
	}
}
