package com.morashid.OnlineShop.auth.security;

import com.morashid.OnlineShop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * CustomUserDetails - wrapper ya User yetu kwa Spring Security.
 * 
 * Spring Security inafanya kazi na "UserDetails" interface, si User entity yetu.
 * Hii class ina-convert User yetu → UserDetails.
 * 
 * Inatoa: username, password, authorities (roles), enabled.
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    
    /**
     * Return authorities (roles) za user.
     * 
     * Spring Security inahitaji "ROLE_" prefix.
     * Mfano: Role.ADMIN → "ROLE_ADMIN"
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    /**
     * Getter ya User entity (kwa access kwenye controllers).
     */
    public User getUser() {
        return user;
    }
}
