package model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenResponse {
	 private String token;

	    // getters and setters
	    public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }

		public TokenResponse(String token) {
			super();
			this.token = token;
		}

}
