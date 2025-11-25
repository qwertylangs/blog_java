package com.example.demo.service.Auth;

import com.example.demo.dtos.auth.JwtRequest;

import com.example.demo.dtos.auth.UserDto;
import com.example.demo.dtos.auth.UserRegistrationRequest;
import com.example.demo.dtos.auth.UserUpdateRequest;
import com.example.demo.exceptions.AppError;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.JwtTokenUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);


    public ResponseEntity<?> authMe() {
        UserDto user = userService.getCurrentUser();

        return new ResponseEntity<>(user, HttpStatus.OK);


    }

    public ResponseEntity<?> createAuthToken(JwtRequest authRequest, HttpServletResponse response) {
        try {
            LOGGER.info("createAuthToken");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"),
                        HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
        }

        var userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        var token = jwtTokenUtils.generateToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from("auth-token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("LAX") // strict
                .maxAge(Duration.ofHours(1)) // из конфига
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().body(Map.of("message", "login successful"));
    }

    public ResponseEntity<?> createNewUser (UserRegistrationRequest registrationRequest, HttpServletResponse response, Boolean isAdmin) {
        if (userService.findByUsername(registrationRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"),
                    HttpStatus.BAD_REQUEST);
        }

        if (isAdmin) {
            userService.createAdminUser(registrationRequest);
        } else {
            userService.createUser(registrationRequest);
        }

        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername(registrationRequest.getUsername());
        jwtRequest.setPassword(registrationRequest.getPassword());
        return createAuthToken(jwtRequest, response);
    }



    public ResponseEntity<?> logout (HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("auth-token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("LAX") // strict
                .maxAge(0) // из конфига
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .body(Map.of("message", "Logout successful"));
    }
}
