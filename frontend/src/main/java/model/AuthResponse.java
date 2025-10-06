package model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder

public class AuthResponse {
	private String token;

	public AuthResponse(String token) {
		this.token = token;
	}
}