package com.frontend.controller;

import java.io.IOError;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@RequestMapping("/page")
public class ExceptionController {
	@ModelAttribute
	public void globalAttributes(Model model) {
		model.addAttribute("pageTitle", "Project Management System");
	}

//	 Handle all Unhandled exceptions
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		model.addAttribute("exception", ex);
		return "exception";
	}

	// Handle 404 Not Found
	@ExceptionHandler(NoHandlerFoundException.class)
	public String handleNotFound(NoHandlerFoundException ex, Model model) {
		model.addAttribute("errorMessage", "Page not found");
		return "exception";
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
		model.addAttribute("errorMessage", "Invalid argument: " + ex.getMessage());
		return "exception";
	}

	@ExceptionHandler(IOError.class)
	public String handleIOError(IllegalArgumentException ex, Model model) {
		model.addAttribute("errorMessage", "Invalid argument: " + ex.toString());
		return "exception";
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
