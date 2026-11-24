package com.globallearning.lms.catalog.web.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}
