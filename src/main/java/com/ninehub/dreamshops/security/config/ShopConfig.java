package com.ninehub.dreamshops.security.config;

import com.ninehub.dreamshops.security.jwt.JwtAuthEntryPoint;
import com.ninehub.dreamshops.security.jwt.AuthTokenFilter;
import com.ninehub.dreamshops.security.jwt.JwtUtils;
import com.ninehub.dreamshops.security.shopUser.ShopUserDetailsService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ShopConfig {
    private final JwtAuthEntryPoint authEntryPoint;
    private final ShopUserDetailsService userDetailsService;
    private final AuthTokenFilter authTokenFilter;

    private static final List<String> SECURED_URLS =
            List.of(
                    "api/v1/cart/**",
                    "api/v1/cartItems/**"
            );

    private static final List<String> PUBLIC_URLS = List.of(
            "/api/v1/auth/**",
            "/api/v1/products/**",
            "/api/v1/categories/**",
            "/api/v1/images/**"
    );

     @Bean
     public ModelMapper modelMapper(){
         return new ModelMapper();
     }

     @Bean
     public PasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder();
     }

//    @Bean
//    public AuthTokenFilter authTokenFilter(JwtUtils jwtUtils,
//                                           ShopUserDetailsService shopUserDetailsService) {
//        return new AuthTokenFilter(jwtUtils, shopUserDetailsService);
//    }

    @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception{
         return authConfiguration.getAuthenticationManager();
     }

     @Bean
     public DaoAuthenticationProvider daoAuthenticationProvider(){
         var authProvider =  new DaoAuthenticationProvider();
         authProvider.setUserDetailsService(userDetailsService);
         authProvider.setPasswordEncoder(passwordEncoder());
         return authProvider;
     }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
         return
                 httpSecurity.csrf(AbstractHttpConfigurer::disable)
                         .authorizeHttpRequests(
                                 authorize ->
                                         authorize
                                                 .requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                                                 .requestMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                                                 .anyRequest().permitAll()
                         )
                         .sessionManagement(session ->
                                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                         .exceptionHandling(exception ->
                                 exception.authenticationEntryPoint(authEntryPoint))

                        .authenticationProvider(daoAuthenticationProvider())
                         .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                         .build();

     }


}
