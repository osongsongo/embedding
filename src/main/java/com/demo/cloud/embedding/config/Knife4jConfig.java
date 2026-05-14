package com.demo.cloud.embedding.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("文本矢量化工具 API")
                        .description("基于 Spring AI 的文本嵌入、存储与相似度检索 API")
                        .version("1.0.0"));
    }
}
