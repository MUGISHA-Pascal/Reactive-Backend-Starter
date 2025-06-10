package com.starter.backend.controllers;

import com.starter.backend.dtos.UserUpdateDto;
import com.starter.backend.models.User;
import com.starter.backend.payload.request.SignupRequest;
import com.starter.backend.services.UserService;
import com.starter.backend.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<User>> createUser(@RequestBody SignupRequest signupRequest) {
        return userService.createUser(signupRequest)
            .map(user -> new ResponseEntity<>(user, HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<User>> getUser(@PathVariable UUID id) {
        return userService.getUser(id)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id)
            .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Flux<User>>> getAllUsers(
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy) {
        return Mono.just(ResponseEntity.ok(userService.getAllUsers(page, size, sortBy)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable UUID id, @RequestBody UserUpdateDto userUpdateRequest) {
        return userService.updateUser(id, userUpdateRequest)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<User>> getCurrentUser() {
        return userService.getLoggedInUser()
            .map(ResponseEntity::ok);
    }
}
