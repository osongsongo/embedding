package com.demo.cloud.embedding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "question cannot be blank")
    @Schema(description = "用户问题", example = "什么是机器学习？")
    private String question;

    @Schema(description = "过滤表达式，如: source_type == 'pdf'，限制检索范围", example = "source_type == 'pdf'")
    private String filterExpression;
}
