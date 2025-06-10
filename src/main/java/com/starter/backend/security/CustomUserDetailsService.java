package com.starter.backend.security;

import com.starter.backend.models.User;
import com.starter.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public Mono<UserDetails> loadUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserPrincipal::create)
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with id: " + id)));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> {
                    System.out.println("User in the user details service: " + user.getPassword());
                    return UserPrincipal.create(user);
                })
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email: " + username)));
    }
}
