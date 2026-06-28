package com.lockify.phase6.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private Long id;
    private Long ownerId;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
