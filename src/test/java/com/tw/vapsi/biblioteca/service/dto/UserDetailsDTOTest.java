package com.tw.vapsi.biblioteca.service.dto;

import com.tw.vapsi.biblioteca.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsDTOTest {

    @Test
    void shouldCreateUserDetailsDTOFromUser() {
        User user = new User(1L,
                "User 1 first name",
                "User 1 last name",
                "email@example.com",
                "password",
                "USER");
        UserDetails userDetails = UserDetailsDTO.create(user);
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = Arrays.asList(
                new SimpleGrantedAuthority("USER")
        );

        assertEquals("email@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(simpleGrantedAuthorities, userDetails.getAuthorities());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isCredentialsNonExpired());
    }
}