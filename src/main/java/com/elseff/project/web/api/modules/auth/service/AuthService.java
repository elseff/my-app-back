package com.elseff.project.web.api.modules.auth.service;

import com.elseff.project.persistense.Role;
import com.elseff.project.persistense.User;
import com.elseff.project.persistense.dao.RoleRepository;
import com.elseff.project.persistense.dao.UserRepository;
import com.elseff.project.web.api.modules.auth.dto.AuthRequest;
import com.elseff.project.web.api.modules.auth.dto.AuthResponse;
import com.elseff.project.web.api.modules.auth.exception.AuthUserNotFoundException;
import com.elseff.project.web.api.modules.auth.exception.AuthenticationException;
import com.elseff.project.web.api.modules.user.dto.UserAllFieldsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Validated
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(@Valid UserAllFieldsDto userAllFieldsDto) {
        if (userRepository.existsByEmail(userAllFieldsDto.getEmail())) {
            log.warn("User with email " + userAllFieldsDto.getEmail() + " already exists");
            throw new AuthenticationException("User with email " + userAllFieldsDto.getEmail() + " already exists");
        }

        User user = modelMapper.map(userAllFieldsDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.getByName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setRoles(roles);
        User userFromDb = userRepository.save(user);
        String token = Base64.encodeBase64String(String.format("%s:%s",
                userAllFieldsDto.getEmail(), userAllFieldsDto.getPassword()).getBytes(StandardCharsets.UTF_8));
        log.info("User with email {} has been successfully registered", userFromDb.getEmail());

        return new AuthResponse(userFromDb.getId(), userAllFieldsDto.getEmail(), token);
    }

    public AuthResponse login(@Valid AuthRequest authRequest) {
        if (!userRepository.existsByEmail(authRequest.getEmail())) {
            log.warn("User with email {} is not found", authRequest.getEmail());
            throw new AuthUserNotFoundException("User with email " + authRequest.getEmail() + " is not found");
        } else {
            User userFromDb = userRepository.getByEmail(authRequest.getEmail());
            if (!passwordEncoder.matches(authRequest.getPassword(), userFromDb.getPassword())) {
                log.warn("Incorrect password");
                throw new AuthenticationException("Incorrect password");
            } else {
                String token = Base64.encodeBase64String(String.format("%s:%s",
                        authRequest.getEmail(), authRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
                log.info("User with email {} has been successfully login", authRequest.getEmail());
                return new AuthResponse(userFromDb.getId(), authRequest.getEmail(), token);
            }
        }
    }

    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return null;
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return user;
    }
}