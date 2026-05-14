package com.demo.cloud.embedding.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>(200, "success", data);
    }

    public static <T> ApiResult<T> success() {
        return success(null);
    }

    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<T>(500, message, null);
    }

    public static <T> ApiResult<T> fail(Integer code, String message) {
        return new ApiResult<T>(code, message, null);
    }
}
