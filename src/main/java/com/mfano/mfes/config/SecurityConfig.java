package com.mfano.mfes.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mfano.mfes.auth.services.CustomDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {
        private final CustomDetailService customDetailService;
        private final PasswordEncoder passwordEncoder;
        private final AuthHandler auth;

        public SecurityConfig(CustomDetailService customDetailService, PasswordEncoder passwordEncoder,
                        AuthHandler auth) {
                this.customDetailService = customDetailService;
                this.passwordEncoder = passwordEncoder;
                this.auth = auth;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth

                                                // Public pages

                                                .requestMatchers("/", "/register", "/login", "/verify", "/forgot",
                                                                "/reset-password",
                                                                "/resend", "/error/403", "/profile", "/css/**",
                                                                "/js/**", "/vendor/**", "/business/**",
                                                                "/image/**")
                                                .permitAll()

                                                // Protected pages
                                                .requestMatchers("/admin/**").hasAnyRole("ADMIN","CEO")
                                                .requestMatchers("/manager/**").hasAnyRole("MANAGER","CEO")
                                                .requestMatchers("/inventory/**").hasAnyRole("PO","CEO")
                                                .requestMatchers("/executive/**").hasRole("CEO")
                                                .anyRequest().authenticated())

                                // 403 Access Denied
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.sendRedirect("/error/403");
                                                }))

                                // Login
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .successHandler(auth)
                                                .failureHandler(auth)
                                                .permitAll())

                                // Logout
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .addLogoutHandler(auth)
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        // =====================================================
        // AUTHENTICATION PROVIDER
        // =====================================================
        @Bean
        AuthenticationProvider authenticationProvider() {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customDetailService);

                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        // =====================================================
        // AUTHENTICATION MANAGER
        // =====================================================
        @Bean
        AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        // IMAGE ROUTE
        @Value("${app.upload-dir}")
        private String uploadDir;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

                Path profilePath = Paths.get(uploadDir, "profiles")
                                .toAbsolutePath()
                                .normalize();

                Path documentPath = Paths.get(uploadDir, "documents")
                                .toAbsolutePath()
                                .normalize();

                Path productPath = Paths.get(uploadDir, "products")
                                .toAbsolutePath()
                                .normalize();

                registry.addResourceHandler("/profiles/**", "/documents/**", "/products/**")
                                .addResourceLocations(profilePath.toUri().toString(), documentPath.toUri().toString(),
                                                productPath.toUri().toString());
        }
}
