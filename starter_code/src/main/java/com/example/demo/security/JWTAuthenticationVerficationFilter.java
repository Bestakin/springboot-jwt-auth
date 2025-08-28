package com.example.demo.security;

import java.io.IOException;   
import java.nio.charset.StandardCharsets;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.example.demo.security.SecurityConstants.*;


public class JWTAuthenticationVerficationFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationVerficationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

@Override
protected void doFilterInternal(HttpServletRequest req,
                                HttpServletResponse res,
                                FilterChain chain) throws IOException, ServletException {

    String header = req.getHeader(HEADER_STRING);
    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      chain.doFilter(req, res);
      return;
    }

    String token = header.substring(TOKEN_PREFIX.length()).trim();

    try {
        Claims claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String user = claims.getSubject();
        if (user != null) {
            var auth = new UsernamePasswordAuthenticationToken(
                user, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            SecurityContextHolder.clearContext();
        }
    } catch (Exception e) {
        SecurityContextHolder.clearContext();
    }

    chain.doFilter(req, res);
}

}
