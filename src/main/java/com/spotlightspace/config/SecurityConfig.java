package com.spotlightspace.config;

import com.spotlightspace.core.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // SessionManagementFilter, SecurityContextPersistenceFilter
                )
                .addFilterBefore(jwtSecurityFilter, SecurityContextHolderAwareRequestFilter.class)
                .formLogin(
                        AbstractHttpConfigurer::disable) // UsernamePasswordAuthenticationFilter, DefaultLoginPageGeneratingFilter 비활성화
                .anonymous(AbstractHttpConfigurer::disable) // AnonymousAuthenticationFilter 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // BasicAuthenticationFilter 비활성화
                .logout(AbstractHttpConfigurer::disable) // LogoutFilter 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/*/auth/**").permitAll()
                        .requestMatchers("/api/*/mail/**").permitAll()
                        .requestMatchers("/api/*/admin/**").hasAuthority(UserRole.Authority.ADMIN)
                        .requestMatchers("/api/*/admin/auth").permitAll()
                        .requestMatchers("/paymentform.html").permitAll()
                        .requestMatchers("/loginform.html").permitAll()
                        .requestMatchers("/callback").permitAll()
                        .requestMatchers("/login/page").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/*/notification/subscribe").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}
