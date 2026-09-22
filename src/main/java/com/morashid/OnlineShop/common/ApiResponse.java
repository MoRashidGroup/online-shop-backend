package com.morashid.OnlineShop.common;


import lombok.*;

/**
 * ApiResponse - wrapper ya jumla kwa responses zote za API.
 * 
 * Inatoa structure moja kwa responses zote, mfano:
 * {
 *   "success": true,
 *   "message": "User registered successfully",
 *   "data": { ... }
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Helper methods kwa urahisi
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
