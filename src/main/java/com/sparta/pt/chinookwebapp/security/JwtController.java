//package com.sparta.pt.chinookwebapp.security;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class JwtController {
//
//    private final JwtTokenService jwtTokenService;
//
//    public JwtController(JwtTokenService jwtTokenService) {
//        this.jwtTokenService = jwtTokenService;
//    }
//
//    @GetMapping("/generate-token")
//    public String generateToken(@RequestParam String username) {
//        return jwtTokenService.generateToken(username);
//    }
//}