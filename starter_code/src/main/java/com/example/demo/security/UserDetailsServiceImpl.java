package com.example.demo.security;

import com.example.demo.model.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User u = userRepository.findByUsername(username);
    if (u == null) throw new UsernameNotFoundException("User not found");
    return new org.springframework.security.core.userdetails.User(
        u.getUsername(),
        u.getPassword(),
        AuthorityUtils.createAuthorityList("ROLE_USER")
    );
  }
}
