package com.demo.cloud.embedding.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmbedRequest {
    @NotBlank(message = "content cannot be blank")
    private String content;
}
