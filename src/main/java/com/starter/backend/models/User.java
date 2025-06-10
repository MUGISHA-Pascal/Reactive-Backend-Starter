package com.starter.backend.models;

import com.starter.backend.enums.EGender;
import com.starter.backend.enums.EStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table("users")
public class User {
    @Id
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private EGender gender;
    private EStatus status = EStatus.ACTIVE;
    private String password;
    private Set<Role> roles = new HashSet<>();

    public User(String email, String firstName, String lastName, String mobile, EGender gender, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.gender = gender;
        this.password = password;
    }
}
