package com.demo.cloud.embedding.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVO {
    private String id;
    private String content;
    private Double score;
    private Map<String, Object> metadata;
}
