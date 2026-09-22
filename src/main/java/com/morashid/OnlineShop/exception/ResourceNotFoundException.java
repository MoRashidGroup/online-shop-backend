package com.morashid.OnlineShop.exception;

/**
 * Custom exception - inatumika wakati resource haipatikani.
 * 
 * Mfano: User mwenye id 5 haipo → tupa ResourceNotFoundException("User", "id", 5L).
 * 
 * RuntimeException - haihitaji kusema "throws" kwenye method signature.
 * GlobalExceptionHandler ita-catch na kurudisha HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Helper constructor - inaunda message nzuri automatically.
     * Mfano: new ResourceNotFoundException("User", "id", 5L)
     *        → "User not found with id : '5'"
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
