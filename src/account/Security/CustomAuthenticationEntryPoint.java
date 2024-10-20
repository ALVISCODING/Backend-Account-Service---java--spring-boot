package account.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom entry point for handling authentication failures.
 *
 * <p>This class implements the <code>AuthenticationEntryPoint</code> interface to
 * provide a custom response when an unauthenticated user attempts to access a
 * protected resource. It sets the HTTP response status to 401 Unauthorized
 * </p>
 *
 * <p>It can also be used to log unauthorized access attempts for auditing or
 * security monitoring purposes.</p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Set the response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json"); // Set content type

        // Create a JSON response object
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        jsonResponse.put("error", "Unauthorized");
        jsonResponse.put("message", authException.getMessage());
        jsonResponse.put("path", request.getRequestURI());

        // JSON response to the output stream
        ObjectMapper objectMapper = new ObjectMapper();
        response.getOutputStream().print(objectMapper.writeValueAsString(jsonResponse));
    }
    }

