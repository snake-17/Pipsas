package com.alexis.picsas.web.config;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/api/auth/*").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/pizzas").hasAuthority("pizza:read")
                            .requestMatchers(HttpMethod.GET,"/api/pizzas/available").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/pizzas/with/*").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/pizzas/without/*").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/pizzas/cheapest/*").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/pizzas/name/*").permitAll()
                            .requestMatchers(HttpMethod.GET,"/api/pizzas/*").hasAnyRole("ADMIN","CUSTOMER","EMPLOYEE")
                            .requestMatchers(HttpMethod.PUT,"/api/pizzas").hasAuthority("pizza:update")
                            .requestMatchers(HttpMethod.PUT,"/api/pizzas/*").hasAuthority("pizza:update_price")
                            .requestMatchers(HttpMethod.POST,"/api/pizzas").hasAuthority("pizza:create")
                            .requestMatchers(HttpMethod.DELETE,"/api/pizzas/*").hasAuthority("pizza:delete")
                            .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.POST, "/api/orders/*/items").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.PATCH, "/api/orders/*/items/*").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.DELETE, "/api/orders/*/items/*").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.POST, "/api/orders-pizza").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.POST, "/api/orders-pizza/*/items").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.PATCH, "/api/orders-pizza/*/items").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.DELETE, "/api/orders-pizza/*/items/*").hasRole("CUSTOMER")
                            .requestMatchers("/api/orders-pizza/**").hasAnyRole("ADMIN", "CUSTOMER")
                            .requestMatchers("/api/orders/random").hasAuthority("random_order")
                            .requestMatchers("/api/customer/**").hasAnyRole("ADMIN","CUSTOMER")
                            .requestMatchers("/api/orders/**").hasRole("ADMIN")
                            .requestMatchers("/api/config/toEmployee/*").hasRole("ADMIN")
                            .requestMatchers(
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/swagger-ui.html"
                            ).permitAll()
                            .anyRequest().authenticated();
                        }
                ).csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
//    @Bean
//    public UserDetailsService memoryUsers(){
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("ADMIN")
//                .build();
//        UserDetails customer = User.builder()
//                .username("customer")
//                .password(passwordEncoder().encode("customer123"))
//                .roles("CUSTOMER")
//                .build();
//        return new InMemoryUserDetailsManager(admin,customer);
//    }
    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
