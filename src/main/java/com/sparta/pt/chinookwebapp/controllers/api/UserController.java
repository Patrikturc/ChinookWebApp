package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.LoginDTO;
import com.sparta.pt.chinookwebapp.models.Role;
import com.sparta.pt.chinookwebapp.models.User;
import com.sparta.pt.chinookwebapp.repositories.RoleRepository;
import com.sparta.pt.chinookwebapp.repositories.UserRepository;
import com.sparta.pt.chinookwebapp.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/logins")
public class UserController {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists!";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Role not found")));
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"));

        if (isAdmin) {
            String token = jwtTokenService.generateToken(authentication.getName());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.ok("Welcome to Chinook API!");
        }
    }
}
