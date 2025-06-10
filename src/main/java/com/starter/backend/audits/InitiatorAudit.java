package com.starter.backend.audits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@JsonIgnoreProperties(value = {"createdBy","updatedBy"}, allowGetters = true)
@Getter
@Setter
public class InitiatorAudit extends TimeStampAudit {
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column("created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column("updated_by")
    private UUID updatedBy;
}
