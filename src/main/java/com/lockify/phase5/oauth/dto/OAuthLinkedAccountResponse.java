package com.lockify.phase5.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLinkedAccountResponse {

    private Long id;
    private String provider;
    private String email;
    private Instant linkedAt;
}
