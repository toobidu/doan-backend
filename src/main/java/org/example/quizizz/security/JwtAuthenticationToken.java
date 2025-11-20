package org.example.quizizz.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final String typeAccount;

    public JwtAuthenticationToken(Long userId, String typeAccount, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.typeAccount = typeAccount;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    public String getTypeAccount() {
        return typeAccount;
    }
}
