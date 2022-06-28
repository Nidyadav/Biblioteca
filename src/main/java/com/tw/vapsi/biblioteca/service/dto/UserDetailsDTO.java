package com.tw.vapsi.biblioteca.service.dto;

import com.tw.vapsi.biblioteca.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsDTO implements UserDetails {

    private final String userName;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private static final List<SimpleGrantedAuthority> simpleGrantedAuthorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_LIBRARIAN")
    );

    public UserDetailsDTO(String userName, String password, List<SimpleGrantedAuthority> authorities) {

        this.userName = userName;
        this.password = password;
        this.authorities = authorities;
    }



    public static UserDetails create(User user) {

        return new UserDetailsDTO(user.getEmail(), user.getPassword(), simpleGrantedAuthorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsDTO that = (UserDetailsDTO) o;
        boolean hasSameName = Objects.equals(userName, that.userName);
        boolean hasSamePassword = Objects.equals(password, that.password);
        boolean hasSameAuthorities = Objects.equals(authorities, that.authorities);
        return hasSameName && hasSamePassword && hasSameAuthorities;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, authorities);
    }
}
