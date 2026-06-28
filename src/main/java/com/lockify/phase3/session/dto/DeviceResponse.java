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
public class DeviceResponse {

    private String deviceId;
    private String deviceName;
    private boolean trusted;
    private Instant lastUsedAt;
    private Instant createdAt;
}
