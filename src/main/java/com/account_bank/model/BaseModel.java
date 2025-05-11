package com.account_bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@MappedSuperclass
@Data
public abstract class BaseModel {

    @JsonProperty("created_at")
    @Column(name = "CREATED_AT", nullable = false)
    private Timestamp createdAt;

    @JsonProperty("deleted_at")
    @Column(name = "DELETED_AT")
    private Timestamp deletedAt;

    @PrePersist
    void onCreate() {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
