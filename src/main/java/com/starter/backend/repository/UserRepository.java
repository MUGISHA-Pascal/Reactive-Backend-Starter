package com.starter.backend.repository;

import com.starter.backend.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE mobile = :mobile")
    Mono<User> findByMobile(String mobile);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE mobile = :mobile)")
    Mono<Boolean> existsByMobile(String mobile);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);

    Flux<User> findAllBy(Pageable pageable);
}
