package com.lockify.phase7.hardening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEventResponse {

    private Long id;
    private Long userId;
    private String eventType;
    private String severity;
    private String ipAddress;
    private String location;
    private String details;
    private Instant createdAt;
}
