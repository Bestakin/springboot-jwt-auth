package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfiguration {

    @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder enc) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(uds);
    provider.setPasswordEncoder(enc);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      org.springframework.security.config.annotation.web.builders.HttpSecurity http,
      AuthenticationManager authManager,
      AuthenticationProvider authProvider
  ) throws Exception {

    // login issues JWT, verification validates JWT on other requests
    var loginFilter  = new JWTAuthenticationFilter(authManager);
    var verifyFilter = new JWTAuthenticationVerficationFilter(authManager);
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authenticationProvider(authProvider)  // <-- register your DaoAuthenticationProvider
      .authorizeHttpRequests(reg -> reg
          .requestMatchers(HttpMethod.POST,
              SecurityConstants.SIGN_UP_URL,  // "/api/user/create"
              SecurityConstants.LOGIN_URL     // "/login"
          ).permitAll()
          .anyRequest().authenticated()
      )
      .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(verifyFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
