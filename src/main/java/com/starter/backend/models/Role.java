package com.starter.backend.models;

import com.starter.backend.enums.ERoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table("roles")
public class Role {
    @Id
    private UUID id;
    private ERoleType name;

    public Role(ERoleType name) {
        this.name = name;
    }
}
