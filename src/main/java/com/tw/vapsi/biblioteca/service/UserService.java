package com.tw.vapsi.biblioteca.service;

import com.tw.vapsi.biblioteca.model.User;
import com.tw.vapsi.biblioteca.repository.UserRepository;
import com.tw.vapsi.biblioteca.service.dto.UserDetailsDTO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
            Optional<User> user = userRepository.findByEmail(username);
            if(!user.isPresent()){
                throw new UsernameNotFoundException(String.format("No user exists with username : %s", username));
            }
            return user.map(UserDetailsDTO::create).get();
    }

    public User save(String firstName, String lastName, String email, String password) {
        String encodePassword = bCryptPasswordEncoder.encode(password);
        User user = new User(firstName, lastName, email, encodePassword);
        return userRepository.save(user);
    }

    public UserDetails login(String userName,String password) throws UsernameNotFoundException,BadCredentialsException{
        UserDetails user = loadUserByUsername(userName);
        if(!user.getPassword().equals(password)) {
           throw new BadCredentialsException(String.format("Invalid Password"));
        }
        return user;
    }
}
