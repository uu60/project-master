package com.dekopon.authentication.vo;

import com.dekopon.authentication.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author dekopon
 * @since 2023/6/19 22:16
 */
public class AccountUserDetails extends AccountEntity implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return expired == 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsExpired == 0;
    }

    @Override
    public boolean isEnabled() {
        return enabled == 1;
    }
}
