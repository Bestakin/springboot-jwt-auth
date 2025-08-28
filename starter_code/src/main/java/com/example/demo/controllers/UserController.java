package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/user")
@Validated

public class UserController {
	private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final PasswordEncoder passwordEncoder;

  public UserController(UserRepository userRepository,
                        CartRepository cartRepository,
                        PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.cartRepository = cartRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<User> findById(@PathVariable Long id) {
    return ResponseEntity.of(userRepository.findById(id));
  }

  @GetMapping("/{username}")
  public ResponseEntity<User> findByUserName(@PathVariable String username) {
    User user = userRepository.findByUsername(username);
    return (user == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest req) {
  
    if (req.getUsername() == null || req.getUsername().isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is required.");
    }
    String password = req.getPassword();
    if (password == null || password.length() < 7) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 7 characters.");
    }
    if (!Objects.equals(req.getPassword(), req.getConfirmPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match.");
    }
    if (userRepository.findByUsername(req.getUsername()) != null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
    }

    // Create user & cart
    User user = new User();
    user.setUsername(req.getUsername());

    Cart cart = new Cart();
    cartRepository.save(cart);
    user.setCart(cart);

    byte[] saltBytes = new byte[16];
    new SecureRandom().nextBytes(saltBytes);
    String salt = Base64.getEncoder().withoutPadding().encodeToString(saltBytes);
    user.setSalt(salt);

    user.setPassword(passwordEncoder.encode(password));

    userRepository.save(user);

    return ResponseEntity.ok(Map.of(
        "id", user.getId(),
        "username", user.getUsername(),
        "message", "User created successfully"
    ));
  }
}
