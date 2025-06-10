package com.starter.backend.services;

import com.starter.backend.dtos.UserUpdateDto;
import com.starter.backend.exceptions.ApiRequestException;
import com.starter.backend.exceptions.AppException;
import com.starter.backend.exceptions.ResourceNotFoundException;
import com.starter.backend.enums.ERoleType;
import com.starter.backend.models.Role;
import com.starter.backend.models.User;
import com.starter.backend.models.UserRole;
import com.starter.backend.repository.RoleRepository;
import com.starter.backend.repository.UserRepository;
import com.starter.backend.repository.UserRoleRepository;
import com.starter.backend.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.starter.backend.payload.request.SignupRequest;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> createUser(SignupRequest signupRequest) {
        return userRepository.findByEmail(signupRequest.getEmail())
                .flatMap(existingUser -> Mono.<User>error(new ApiRequestException("Email already in use")))
                .switchIfEmpty(
                    userRepository.findByMobile(signupRequest.getMobile())
                        .flatMap(existingUser -> Mono.<User>error(new ApiRequestException("Mobile number already in use")))
                        .switchIfEmpty(
                            roleRepository.findByName(ERoleType.USER)
                                .flatMap(role -> {
                                    User user = new User();
                                    user.setFirstName(signupRequest.getFirstName());
                                    user.setLastName(signupRequest.getLastName());
                                    user.setEmail(signupRequest.getEmail());
                                    user.setMobile(signupRequest.getMobile());
                                    user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                                    user.addRole(role);
                                    return userRepository.save(user);
                                })
                        )
                );
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUser(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId.toString())));
    }

    public Mono<Void> deleteUser(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId.toString())))
                .flatMap(user -> userRepository.delete(user));
    }

    public Flux<User> getAllUsers(int page, int size, String column) {
        Constants.validatePageNumberAndPageSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, column);
        return userRepository.findAllBy(pageable);
    }

    public Mono<User> getLoggedInUser() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ApiRequestException("User with email " + email + " not found")));
    }

    public Mono<User> updateUser(UUID userId, UserUpdateDto userUpdateRequest) {
        return getLoggedInUser()
                .flatMap(loggedInUser -> userRepository.findById(userId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId.toString())))
                        .flatMap(user -> {
                            if (!loggedInUser.getId().equals(user.getId())) {
                                return Mono.error(new AppException("You are not authorized to update this user"));
                            }
                            return userRepository.findByMobile(userUpdateRequest.getMobile())
                                    .filter(existingUser -> !existingUser.getId().equals(userId))
                                    .hasElement()
                                    .flatMap(exists -> {
                                        if (exists) {
                                            return Mono.error(new AppException("Mobile number already in use"));
                                        }
                                        user.setFirstName(userUpdateRequest.getFirstName());
                                        user.setLastName(userUpdateRequest.getLastName());
                                        user.setMobile(userUpdateRequest.getMobile());
                                        user.setGender(userUpdateRequest.getGender());
                                        user.setEmail(userUpdateRequest.getEmail());
                                        return userRepository.save(user);
                                    });
                        }));
    }

    public Mono<User> updateUser(UUID userId, User user) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId.toString())))
                .flatMap(existingUser -> {
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setMobile(user.getMobile());
                    existingUser.setGender(user.getGender());
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<Boolean> existsByMobile(String mobile) {
        return userRepository.existsByMobile(mobile);
    }
}
