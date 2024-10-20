package account.Security;

import account.audit.handler.CustomLoginFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
/**
 * Security configuration class for the Spring application.
 *
 * <p>This class is responsible for configuring the security settings for the
 * web application, including authentication and authorization mechanisms.</p>
 *
 * <p>The {@link EnableWebSecurity} annotation enables Spring Security’s
 * web security support and provides the Spring MVC integration.</p>
 *
 * <p>The {@link EnableGlobalMethodSecurity(prePostEnabled = true)} annotation
 * allows for method-level security using the @PreAuthorize and @PostAuthorize
 * annotations, enabling fine-grained control over access to specific methods.</p>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Securityconfig {

    //CustomAccessDeniedHandler

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    public Securityconfig(CustomAccessDeniedHandler customAccessDeniedHandler, @Lazy CustomLoginFailureHandler customLoginFailureHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customLoginFailureHandler = customLoginFailureHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //.httpBasic(Customizer.withDefaults())
                .httpBasic(basic ->
                        basic.authenticationEntryPoint(customAuthenticationEntryPoint) // Set the custom entry point
                )
                //.exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint)) // Handle auth errors
                .csrf(csrf -> csrf.disable()) // For Postman
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .authorizeHttpRequests(auth -> auth  // manage access
                                .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/actuator/shutdown").permitAll() // Allow signup without authentication
                                .requestMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyAuthority("ROLE_ACCOUNTANT", "ROLE_USER", "ROLE_ADMINISTRATOR") // Allow role-based access
                                .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyAuthority("ROLE_USER","ROLE_ACCOUNTANT") // User access for payment records
                                .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasAuthority("ROLE_ACCOUNTANT") // Accountant access for payment updates
                                .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasAuthority("ROLE_ACCOUNTANT") // Accountant access for creating payments
                                .requestMatchers(HttpMethod.GET, "/api/admin/user/").hasAuthority("ROLE_ADMINISTRATOR") // Admin access to user records
                                // use double ** is used to match any number of path segments after /api/admin/user/,
                                // allowing the dynamic email part (e.g., /api/admin/user/someone@example.com) to be handled correctly.
                                .requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority("ROLE_ADMINISTRATOR") // Admin access for deleting users
                                .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasAuthority("ROLE_ADMINISTRATOR") // Admin access for role updates
                                .requestMatchers(HttpMethod.GET, "/api/security/events/*").hasAuthority("ROLE_AUDITOR") // Audit access for role updates
                                .anyRequest().authenticated()

                        // other matchers
                ).exceptionHandling(ex -> ex
                    .accessDeniedHandler(customAccessDeniedHandler) // Register Custom Access Denied Handler
                ).sessionManagement(sessions -> sessions
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                        ).formLogin(form -> form
                                //.loginPage("/api/auth/login") // Customize the login page endpoint
                            .loginProcessingUrl("/login") // This is the URL where login requests are submitted
                            //.failureHandler(customLoginFailureHandler) // Set the custom failure handler
                            .permitAll() // Allow access to the login page (default login)
                        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
