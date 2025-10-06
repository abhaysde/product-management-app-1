package com.frontend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configurations {

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/login", "/signup", "/take-login", "/take-signup")
								.permitAll().anyRequest().authenticated() // all other endpoints need valid JWT
				)

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.logout(logout -> logout.logoutUrl("/logout") // Optional: for frontend-controlled logout
						.logoutSuccessUrl("/login") // or "/take-logout" if you need redirect
						.deleteCookies("token") // deletes the token cookie
						.invalidateHttpSession(true).clearAuthentication(true))

				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
					response.sendRedirect("/login");
				}));

		// Your custom JWT filter
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**");
	}

}
