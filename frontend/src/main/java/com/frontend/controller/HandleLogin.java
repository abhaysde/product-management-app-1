package com.frontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import model.Login;
import model.Signup;
import model.TokenResponse;


@Controller

public class HandleLogin {

	@Autowired
	private RestTemplate restTemplate;
	
	/*
	 * This is the actual urls for the authentication and authorization private
	 * final String AUTH_URL = "http://localhost:8083/auth";
	 */
	
//	API-Gateway URL which routes to the actual URLs
	private final String AUTH_URL = "http://localhost:8080/auth";
	

	@GetMapping(value = { "/", "/home" })
	public String handleHomePage(Model model) {
		return "homepage";
	}

	@GetMapping("/signup")
	public String takeSignup() {
		return "signup";
	}

	@PostMapping("/take-signup")
	public String handleSignup(@ModelAttribute Signup signup, Model model, RedirectAttributes redirectAttributes) {
		try {
			restTemplate.postForEntity(AUTH_URL + "/signup", signup, Void.class);
			model.addAttribute("msg", "Signup successful!");
		} catch (HttpClientErrorException e) {
			model.addAttribute("msg", "Signup failed: " + e.getResponseBodyAsString());
		} catch (Exception e) {
			model.addAttribute("msg", "Server error. Please try again later.");
		}
		return "signup";
	}

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		return "login";
	}

	@PostMapping("/take-login")
	public String handleLogin(@ModelAttribute Login login, HttpServletResponse response, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Login> request = new HttpEntity<>(login, headers);

			ResponseEntity<TokenResponse> apiResponse = restTemplate.postForEntity(AUTH_URL + "/login", request,
					TokenResponse.class);

			TokenResponse token = apiResponse.getBody();

			if (apiResponse.getStatusCode().is2xxSuccessful() && token != null && token.getToken() != null) {
				Cookie cookie = new Cookie("token", token.getToken());
				cookie.setHttpOnly(true);
				cookie.setSecure(false); // ⚠️ Set to true in production (HTTPS)
				cookie.setPath("/");
				cookie.setMaxAge(15 * 60); // 15 minutes
				response.addCookie(cookie);

				return "redirect:/home";
			}

			model.addAttribute("error", "Login failed. Please check your credentials.");
			return "login";

		} catch (HttpClientErrorException e) {
			model.addAttribute("error", "Invalid credentials.");
			return "login";
		} catch (Exception e) {
			model.addAttribute("error", "Server error. Please try again later.");
			return "login";
		}
	}

	@GetMapping("/take-logout")
	public String handleLogout(HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
		Cookie cookie = new Cookie("token", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		redirectAttributes.addFlashAttribute("msg", "You are successfully logged out from our website! Thank you");
		return "redirect:/login";
	}

	@ModelAttribute
	public void addCommonAttributes(Model model, @CookieValue(value = "token", required = false) String token) {
		model.addAttribute("pageTitle", "Project Management System");

		if (token != null && !token.isEmpty()) {
			model.addAttribute("action", "Logout");
		} else {
			model.addAttribute("action", "Login");
		}
	}

}
