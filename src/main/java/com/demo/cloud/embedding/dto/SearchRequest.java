package com.demo.cloud.embedding.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequest {
    @NotBlank(message = "query cannot be blank")
    private String query;
    private Integer topK = 5;
    private Double threshold = 0.5;
}
