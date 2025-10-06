package com.gspann.auth_service.jwt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException, java.io.IOException {
	    
	    String authHeader = request.getHeader("Authorization");

	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	    	
	    	System.out.println("JwtFilter.doFilterInternal()" + authHeader);
	        String token = authHeader.substring(7);
	        if (jwtUtil.isTokenValid(token)) {
	            String username = jwtUtil.extractUsername(token);

	            // No roles or authorities
	            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
	                    username, "", List.of());

	            UsernamePasswordAuthenticationToken authToken =
	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

	            SecurityContextHolder.getContext().setAuthentication(authToken);
	            System.out.println("auth token : "+ authToken);
	        }
	    }

	    filterChain.doFilter(request, response);
	}

}
