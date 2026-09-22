package com.morashid.OnlineShop.exception;


/**
 * Custom exception - inatumika kwa validation errors au business rule violations.
 * 
 * Mfano:
 *   - Email already exists
 *   - Stock quantity cannot be negative
 *   - Invalid credentials
 * 
 * GlobalExceptionHandler ita-catch na kurudisha HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}