package com.example.demo.security;

import static com.example.demo.security.SecurityConstants.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;



public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    private final AuthenticationManager authenticationManager;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
    setFilterProcessesUrl(LOGIN_URL); // "/login"
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {
      Map<String, String> creds =
          new ObjectMapper().readValue(req.getInputStream(), new TypeReference<>() {});
      var authToken = new UsernamePasswordAuthenticationToken(
          creds.get("username"),
          creds.get("password")
      );
      return authenticationManager.authenticate(authToken);
    } catch (IOException e) {
      throw new AuthenticationServiceException("Invalid login payload", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
                                          HttpServletResponse res,
                                          FilterChain chain,
                                          Authentication auth) throws IOException, ServletException {
    String username = ((User) auth.getPrincipal()).getUsername();

    String jwt = Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    res.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt); 
    res.setContentType("application/json");
    res.getWriter().write("{\"token\":\"" + TOKEN_PREFIX + jwt + "\"}");
  }
}
