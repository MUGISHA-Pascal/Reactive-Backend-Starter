package com.starter.backend.controllers;

import com.starter.backend.payload.request.LoginRequest;
import com.starter.backend.payload.request.SignupRequest;
import com.starter.backend.payload.response.JwtAuthResponse;
import com.starter.backend.payload.response.Response;
import com.starter.backend.security.JwtTokenProvider;
import com.starter.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public Mono<ResponseEntity<JwtAuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        )
        .flatMap(auth -> tokenProvider.generateToken(auth)
            .map(jwt -> ResponseEntity.ok(new JwtAuthResponse(jwt))));
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<Response>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return userService.createUser(signUpRequest)
            .map(user -> ResponseEntity.ok(new Response("User registered successfully!")))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                .body(new Response("Error: " + e.getMessage()))));
    }
}
