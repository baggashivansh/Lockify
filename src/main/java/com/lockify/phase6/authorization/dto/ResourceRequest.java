package com.lockify.phase6.authorization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourceRequest {

    @NotBlank(message = "Title required hai")
    private String title;

    private String content;
}
