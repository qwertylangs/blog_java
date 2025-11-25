package com.example.demo.service.Auth;

import com.example.demo.dtos.auth.UserDto;
import com.example.demo.dtos.auth.UserRegistrationRequest;
import com.example.demo.dtos.auth.UserUpdateRequest;
import com.example.demo.model.MyUser;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public Optional<MyUser> findByUsername (String username) {
        return userRepository.findByUsername(username);
    }

    public UserDto getCurrentUser () {
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();


        Optional<MyUser> userOptional = userRepository.findByUsername(username);


        if ( userOptional.isEmpty()) return null;
        MyUser user = userOptional.get();

        String[] roles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), roles, user.getAvatarUrl());
    }

    public MyUser createNewUser (UserRegistrationRequest userRegistrationRequest, Role role) {

        MyUser user = new MyUser();
        user.setUsername(userRegistrationRequest.getUsername());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        List<Role> list = List.of(role);
        user.setRoles(list);
        return userRepository.save(user);
    };

    public void createAdminUser (UserRegistrationRequest userRegistrationRequest) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new IllegalArgumentException("Role ROLE_ADMIN not found"));
        createNewUser(userRegistrationRequest, adminRole);
    }

    public void createUser (UserRegistrationRequest userRegistrationRequest) {
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new IllegalArgumentException("Role ROLE_USER not found"));
        createNewUser(userRegistrationRequest, userRole);
    }

    public ResponseEntity<?> updateUsers(UserUpdateRequest userUpdateRequest, Long userId) {
        MyUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("USER not found"));
        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().isBlank()
                && !userRepository.existsByUsername(userUpdateRequest.getUsername()) ) {
            // проверять на уникальность
            user.setUsername(userUpdateRequest.getUsername());
        }
        else{
            return ResponseEntity.status(409).body("USER IS ALREADY EXISTS");
        }

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isBlank()) {
            // проверять на уникальность АХАХХА
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isBlank()
                && !userRepository.existsByEmail(userUpdateRequest.getEmail())) {
            // проверять на уникальность
            user.setEmail(userUpdateRequest.getEmail());
        }
        else{
            return ResponseEntity.status(409).body("EMAIL IS ALREADY EXISTS");
        }
        if (userUpdateRequest.getAvatarUrl() != null && !userUpdateRequest.getAvatarUrl().isBlank() ) {
            user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        }

        MyUser updatedUser = userRepository.save(user);

        String[] roles = updatedUser.getRoles().stream().map(Role::getName).toArray(String[]::new);
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), roles, user.getAvatarUrl());
        return ResponseEntity.ok(userDto);
    }


}
