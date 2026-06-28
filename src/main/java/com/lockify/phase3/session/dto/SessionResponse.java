package com.lockify.phase3.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private String sessionId;
    private String deviceName;
    private String browser;
    private String os;
    private String ipAddress;
    private boolean active;
    private Instant loginAt;
    private Instant lastActivity;
}
