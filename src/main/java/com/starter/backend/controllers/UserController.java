package com.starter.backend.controllers;

import com.starter.backend.dtos.UserUpdateDto;
import com.starter.backend.exceptions.ApiRequestException;
import com.starter.backend.exceptions.AppException;
import com.starter.backend.exceptions.ResourceNotFoundException;
import com.starter.backend.models.User;
import com.starter.backend.payload.request.SignupRequest;
import com.starter.backend.payload.response.Response;
import com.starter.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Response>> createUser(@Valid @RequestBody SignupRequest signupRequest) {
        return userService.createUser(signupRequest)
            .map(user -> ResponseEntity.ok(new Response("User created successfully!")))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                .body(new Response("Error: " + e.getMessage()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable UUID id) {
        return userService.getUser(id)
            .map(ResponseEntity::ok)
            .onErrorResume(ResourceNotFoundException.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<User>> getCurrentUser() {
        return userService.getLoggedInUser()
            .map(ResponseEntity::ok)
            .onErrorResume(ApiRequestException.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public Mono<ResponseEntity<Response>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDto userUpdateRequest) {
        return userService.updateUser(id, userUpdateRequest)
            .map(user -> ResponseEntity.ok(new Response("User updated successfully!")))
            .onErrorResume(e -> {
                if (e instanceof ResourceNotFoundException) {
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response("Error: " + e.getMessage())));
                } else if (e instanceof AppException) {
                    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new Response("Error: " + e.getMessage())));
                }
                return Mono.just(ResponseEntity.badRequest()
                    .body(new Response("Error: " + e.getMessage())));
            });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Response>> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id)
            .then(Mono.just(ResponseEntity.ok(new Response("User deleted successfully!"))))
            .onErrorResume(ResourceNotFoundException.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response("Error: " + e.getMessage()))));
    }

    @GetMapping
    public Flux<ResponseEntity<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy) {
        return userService.getAllUsers(page, size, sortBy)
                .map(ResponseEntity::ok);
    }
}
