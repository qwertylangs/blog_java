package com.example.demo.controller;

import com.example.demo.dtos.auth.JwtRequest;
import com.example.demo.dtos.auth.UserDto;
import com.example.demo.dtos.auth.UserRegistrationRequest;
import com.example.demo.dtos.auth.UserUpdateRequest;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Auth.AuthService;
import com.example.demo.service.Auth.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserService userService;


    @GetMapping("/users")
    public List<MyUser> findAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/auth-me")
    public ResponseEntity<?> checkAuthentication() {
        return authService.authMe();
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticate (@RequestBody JwtRequest authRequest, HttpServletResponse response) {
        return authService.createAuthToken(authRequest, response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        UserDto currentUser = userService.getCurrentUser();
        return userService.updateUsers(userUpdateRequest, currentUser.getId());
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration (@RequestBody UserRegistrationRequest userRegistrationRequest,  HttpServletResponse response) {
        return authService.createNewUser(userRegistrationRequest, response, false);
    }

    @PostMapping("/registration-admin")
    public ResponseEntity<?> registrationAdmin (@RequestBody UserRegistrationRequest userRegistrationRequest,  HttpServletResponse response) {
        return authService.createNewUser(userRegistrationRequest, response, true);
    }




    @GetMapping("/logout")
    public ResponseEntity<?> logout (HttpServletResponse response) {

        return  authService.logout(response);
    };

}
