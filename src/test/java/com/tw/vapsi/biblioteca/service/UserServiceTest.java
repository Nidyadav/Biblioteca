package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.exception.UserAlreadyExistsException;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.repository.UserRepository;
import com.tw.vapsi.biblioteca.service.dto.UserDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;


    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, bCryptPasswordEncoder);
    }

    @Test
    void shouldLoadUserDetailsFromUserName() {
        User user = new User(1L,
                "User 1 first name",
                "User 1 last name",
                "email@example.com",
                "password");
        UserDetails expectedUserDetails = UserDetailsDTO.create(user);
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("email@example.com");

        assertEquals(expectedUserDetails, userDetails);
    }

    @Test
    void shouldThrowUserNotFoundException() {
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException actualException = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("email@example.com")
        );

        assertEquals("No user exists with username : email@example.com", actualException.getMessage());
    }

    @Test
    void shouldSaveTheUserInformation() throws UserAlreadyExistsException {
        User userToBeCreated = new User(
                "Micky",
                "Mouse",
                "micky-mouse@example.com",
                "encoded-password");


        User expectedUser = new User(
                1L,
                "Micky",
                "Mouse",
                "micky-mouse@example.com",
                "encoded-password");
        when(userRepository.save(userToBeCreated)).thenReturn(expectedUser);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encoded-password");

        User actualUser = userService.save("Micky", "Mouse", "micky-mouse@example.com", "password");

        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).save(userToBeCreated);
    }
}