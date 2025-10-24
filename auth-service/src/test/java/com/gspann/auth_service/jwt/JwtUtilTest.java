package com.gspann.auth_service.jwt;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("testUser");
        assertNotNull(token);

        boolean valid = jwtUtil.isTokenValid(token);
        assertTrue(valid);

        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);
    }
 
}
