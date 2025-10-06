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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import model.Product;

@Service
public class ApiService {

	private static final String BASE_URL = "http://localhost:8080/api";

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Creates a new product or updates an existing one based on its ID.
	 */
	public String saveAPI(Product product, String token) {
		HttpEntity<Product> entity = buildEntityWithBody(product, token);

		if (product.getId() == 0) {
			// Create new product
			restTemplate.postForEntity(BASE_URL + "/product", entity, Product.class);
		} else {
			// Update existing product
			restTemplate.exchange(BASE_URL + "/product/" + product.getId(), HttpMethod.PUT, entity, Void.class);
		}

		return "redirect:/page/products";
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

	    ResponseEntity<List<Product>> response = restTemplate.exchange(
	        url,
	        HttpMethod.GET,
	        entity,
	        new ParameterizedTypeReference<>() {}
	    );

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
