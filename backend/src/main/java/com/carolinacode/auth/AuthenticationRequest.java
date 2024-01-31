package com.carolinacode.auth;

public record AuthenticationRequest(
        String username,
        String password

) {
}
