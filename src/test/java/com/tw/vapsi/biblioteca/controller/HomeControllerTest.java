package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.controller.helper.ControllerTestHelper;
import com.tw.vapsi.biblioteca.exception.UserAlreadyExistsException;
import com.tw.vapsi.biblioteca.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest extends ControllerTestHelper {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldShowWelcomeMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateNewUserOnSuccessfulSignUp() throws Exception {

        User newUser = new User("mickey", "mouse", "abc@gmail.com", "abc");
        when(userService.save("mickey", "mouse", "abc@gmail.com", "abc"))
                .thenReturn(newUser);

        mockMvc.perform(post("/addUser")
                        .param("firstName", "mickey")
                        .param("lastName", "mouse")
                        .param("email", "abc@gmail.com")
                        .param("password", "abc"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.view().name("index"));
        verify(userService, times(1)).save("mickey", "mouse", "abc@gmail.com", "abc");
    }

    @Test
    void shouldNotCreateNewUserWithInvalidCredentials() throws Exception {

        mockMvc.perform(post("/addUser")
                        .param("firstname", " ")
                        .param("lastname", " ")
                        .param("email", " ")
                        .param("password", " "))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("firstnameErrorMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lastnameErrorMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailErrorMessage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("passwordErrorMessage"));
    }

    @Test
    void shouldNotCreateNewUserWithInvalidEmailId_1() throws Exception {

        mockMvc.perform(post("/addUser")
                        .param("firstname", "mickey")
                        .param("lastname", "mouse")
                        .param("email", "abc")
                        .param("password", "abcd"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailErrorMessage"));

        verify(userService , times(0)).save("mickey", "mouse", "abc@gmail", "abcd");
    }

    @Test
    void shouldNotCreateNewUserWithInvalidEmailId_2() throws Exception {

        mockMvc.perform(post("/addUser")
                        .param("firstname", "mickey")
                        .param("lastname", "mouse")
                        .param("email", "abc@gmail")
                        .param("password", "abcd"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailErrorMessage"));

        verify(userService , times(0)).save("mickey", "mouse", "abc@gmail", "abcd");
    }
    @Test
    void shouldNotCreateNewUserWithInvalidEmailId_3() throws Exception {

        mockMvc.perform(post("/addUser")
                        .param("firstname", "mickey")
                        .param("lastname", "mouse")
                        .param("email", "abc.com")
                        .param("password", "abcd"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailErrorMessage"));

        verify(userService , times(0)).save("mickey", "mouse", "abc@gmail", "abcd");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"LIBRARIAN"})
    void shouldRedirectToSignUpPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.view().name("signup"));
    }

    @Test
    void shouldNotCreateNewUserAgainWithSameEmailID() throws Exception {

        UserAlreadyExistsException userAlreadyExistsException = new UserAlreadyExistsException("User already exists with that Email Id. Please try again.");
        when(userService.save("mickey", "mouse", "abc@gmail.com", "abc")).thenThrow(userAlreadyExistsException);


        mockMvc.perform(post("/addUser")
                        .param("firstName", "mickey")
                        .param("lastName", "mouse")
                        .param("email", "abc@gmail.com")
                        .param("password", "abc"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.view().name("signup"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userAlreadyExistsErrorMessage"));

        verify(userService, times(1)).save("mickey", "mouse", "abc@gmail.com", "abc");
    }
}