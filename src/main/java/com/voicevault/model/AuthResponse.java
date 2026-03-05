package com.voicevault.model;

public class AuthResponse {
    private String token;
    private String email;
    private String userId;
    private String message;

    public AuthResponse() {}
    public AuthResponse(String token, String email, String userId, String message) {
        this.token = token;
        this.email = email;
        this.userId = userId;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
