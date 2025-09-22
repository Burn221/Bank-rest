package com.example.bankcards.dto.userdto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record AuthUser(Long id, String username,
                       Collection<? extends GrantedAuthority> authorities) {
}
