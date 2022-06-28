package com.tw.vapsi.biblioteca.controller;

import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public User createUser(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String password) {
        return userService.save(firstName, lastName, email, password);
    }


    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, Model model){
        try {
            UserDetails userDetails = userService.login(user.getEmail(), user.getPassword());
            model.addAttribute("userDetails",userDetails);
            return "index";
        }catch(UsernameNotFoundException ex){
            model.addAttribute("error",ex.getMessage());
            return "login";
        }catch (BadCredentialsException ex){
            model.addAttribute("error",ex.getMessage());
            return "login";
        }
    }

}
