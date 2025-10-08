package com.frontend.services;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
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
	public void saveAPI(Product product, String token, MultipartFile image) throws Exception{
		HttpEntity<Product> entity = buildEntityWithBody(product, token);
		
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
//		
		
		  HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	        // Create JSON part for the product
	        HttpHeaders jsonHeaders = new HttpHeaders();
	        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
	        HttpEntity<String> jsonPart = new HttpEntity<>(
	                new ObjectMapper().writeValueAsString(product), jsonHeaders);

	        // Create file part
	        HttpEntity<byte[]> filePart = null;
	        if (image != null && !image.isEmpty()) {
	            HttpHeaders fileHeaders = new HttpHeaders();
	            fileHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
	            filePart = new HttpEntity<>(image.getBytes(), fileHeaders);
	        }

	        // Assemble multipart request
	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("product", jsonPart);
	        if (filePart != null) {
	            body.add("image", new ByteArrayResource(image.getBytes()) {
	                @Override
	                public String getFilename() {
	                    return image.getOriginalFilename();
	                }
	            });
	        }

	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

	        ResponseEntity<Product> response = restTemplate.exchange(
	                BASE_URL + "/product",
	                HttpMethod.POST,
	                requestEntity,
	                Product.class
	        );
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
