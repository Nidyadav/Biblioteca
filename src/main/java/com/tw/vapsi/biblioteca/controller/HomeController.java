package com.tw.vapsi.biblioteca.controller;


import com.tw.vapsi.biblioteca.exception.UserAlreadyExistsException;
import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String base(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to Biblioteca. Your one-stop-shop for great book titles in Bangalore!");
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signup";
    }

    @PostMapping("/addUser")
    public String saveUser(@ModelAttribute("user") User user, Model model,final RedirectAttributes redirectAttributes) throws UserAlreadyExistsException {

        if (checkNewUserDetails(user, model)) {
            try {
                user = userService.save(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
                redirectAttributes.addFlashAttribute("message", "Successfully added..");
                model.addAttribute(user);
                return "successfulsignup";

            } catch (UserAlreadyExistsException userAlreadyExistsException) {
                model.addAttribute("userAlreadyExistsErrorMessage", userAlreadyExistsException.getMessage());
                return "signup";
            }
        }
        return "signup";
    }

    private boolean checkNewUserDetails(User user, Model model) {
        boolean isValidAttribute = true;
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            model.addAttribute("firstnameErrorMessage", "First Name cannot be empty ");
            isValidAttribute = false;
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            model.addAttribute("lastnameErrorMessage", "Last Name cannot be empty");
                isValidAttribute = false;
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("emailErrorMessage", "Email cannot be empty");
                isValidAttribute = false;

        }else{

            String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+.[com]$";
            Pattern pattern=Pattern.compile(regex);
            Matcher matcher=pattern.matcher(user.getEmail());

            if(!matcher.matches()) {
                model.addAttribute("emailErrorMessage", "Please enter valid email.");
                isValidAttribute = matcher.matches();
            }
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            model.addAttribute("passwordErrorMessage", "Password cannot be empty");
            isValidAttribute = false;
        }
        return isValidAttribute;
    }
}
