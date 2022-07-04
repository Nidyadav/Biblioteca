package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.controller.helper.ControllerTestHelper;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
    void shouldCreateNewUserOnSuccesfullSignUp() throws Exception {

        User newUser = new User("mickey","mouse","abc@gmail.com","abc");
        when(userService.save("mickey","mouse","abc@gmail.com","abc"))
                .thenReturn(newUser);

        mockMvc.perform(post("/addUser")
                .param("firstname","mickey")
                .param("lastname","mouse")
                .param("email","abc@gmail.com")
                .param("password","abc"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.view().name("signup"));
   }

   @Test
    void shouldNotCreateNewUserWithInvalidCredentials() throws Exception {

        mockMvc.perform(post("/addUser")
                .param("firstname"," ")
                .param("lastname"," ")
                .param("email"," ")
                .param("password"," "))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("firstnameErrorMessage"))
                        .andExpect(MockMvcResultMatchers.model().attributeExists("lastnameErrorMessage"))
                                .andExpect(MockMvcResultMatchers.model().attributeExists("emailErrorMessage"))
                                        .andExpect(MockMvcResultMatchers.model().attributeExists("passwordErrorMessage"));
   }
}