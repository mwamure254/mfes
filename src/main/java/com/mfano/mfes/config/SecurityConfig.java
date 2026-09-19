package com.mfano.mfes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

import com.mfano.mfes.auth.services.CustomDetailService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomDetailService customDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthHandler auth;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                    "/",
                    "/landing",
                    "/error/**",
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/fonts/**",
                    "/scss/**",
                    "/vendors/**",
                    "/images/**"
                ).permitAll()

                // Protected pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/business/**").hasRole("BDO")
                .requestMatchers("/procurement/**").hasRole("PO")
                .requestMatchers("/executive/**").hasRole("CEO")
                .anyRequest().authenticated()
            )

            // 403 Access Denied
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/error/403");
                })
            )

            // Login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(auth)
                .failureHandler(auth)
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(auth)
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/landing")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customDetailService);

        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
