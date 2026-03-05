package com.voicevault.controller;

import com.voicevault.config.JwtUtil;
import com.voicevault.model.AuthRequest;
import com.voicevault.model.AuthResponse;
import com.voicevault.model.User;
import com.voicevault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody AuthRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(encoder.encode(request.getPassword()));
            userRepository.save(user);

            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getId(), "Account created successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
            }

            User user = userOpt.get();
            if (!encoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
            }

            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getId(), "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }
}
