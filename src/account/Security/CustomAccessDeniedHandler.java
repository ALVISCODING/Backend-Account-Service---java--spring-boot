package account.Security;

import account.audit.events.AccessDeniedEvent;
import account.audit.events.UserActionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
/**
 * Custom handler for access denied scenarios.
 *
 * <p>This class implements AccessDeniedHandler to provide a custom response
 * when access to a resource is denied.</p>
 */
    @Component
    public class CustomAccessDeniedHandler implements AccessDeniedHandler {
        private final ApplicationEventPublisher eventPublisher;

        public CustomAccessDeniedHandler(ApplicationEventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
        }

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
                throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("timestamp", LocalDateTime.now().toString());
            errorDetails.put("status", HttpServletResponse.SC_FORBIDDEN);
            errorDetails.put("error", "Forbidden");
            errorDetails.put("message", "Access Denied!");  // Custom message
            errorDetails.put("path", request.getRequestURI());

            //log the event
            //subject is user
            //object is path
            String action = "ACCESS_DENIED";
            String subject = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName().toLowerCase() : "Anonymous";
            String url = removeLastChar(request.getRequestURI());

            AccessDeniedEvent accessDeniedEvent = new AccessDeniedEvent(this,action,subject,url,url);
            eventPublisher.publishEvent(accessDeniedEvent);

            // Convert a map to JSON and send it in the response
            String jsonResponse = new ObjectMapper().writeValueAsString(errorDetails);
            response.getWriter().write(jsonResponse);
        }

        private String removeLastChar(String str) {
            if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '/') {
                return str.substring(0, str.length() - 1); // Remove last character only if it is '/'
            } else {
                return str; // Return the original string if it's null, empty, or doesn't end with '/'
            }
        }

        }


