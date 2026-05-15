package com.demo.cloud.embedding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteByFilterRequest {
    @NotBlank(message = "filterExpression cannot be blank")
    @Schema(description = "过滤表达式，如: source_type == 'pdf'", example = "source_type == 'pdf'")
    private String filterExpression;
}
