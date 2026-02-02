package com.zezame.lipayz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {
    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private UUID createdBy;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private UUID updatedBy;

    @Column(nullable = false)
    @Version
    private Integer version;

    public BaseModel() {
    }
}
