package com.demo.cloud.embedding.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {
    private Integer totalCount;
    private Integer successCount;
    private List<String> sources;
}
