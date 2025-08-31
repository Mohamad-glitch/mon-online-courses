package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.Requests.LoginRequest;
import com.example.mononlinecourses.dto.Requests.NewPassword;
import com.example.mononlinecourses.dto.Requests.RegisterRequest;
import com.example.mononlinecourses.dto.Requests.ResetPasswordRequest;
import com.example.mononlinecourses.dto.responses.LoginResponse;
import com.example.mononlinecourses.enums.Roles;
import com.example.mononlinecourses.exception.UserNotFound;
import com.example.mononlinecourses.exception.UserPasswordOrEmailIsNotCorrect;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.model.User;
import com.example.mononlinecourses.utils.JwtUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailSenderService mailSender;
    private final UserSessionService userSessionService;


    public AuthService(JwtUtils jwtUtils, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, MailSenderService mailSender, UserSessionService userSessionService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.userSessionService = userSessionService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> user = userService.findUserByEmail(loginRequest.email());

        if (user.isEmpty()) {
            throw new UserNotFound("user with this email " + loginRequest.email() + " was not found");
        } else if (!bCryptPasswordEncoder.matches(loginRequest.password(), user.get().getPassword())) {
            throw new UserPasswordOrEmailIsNotCorrect("password or password is incorrect");
        } else {
            return new LoginResponse(jwtUtils.generateToken(user.get().getEmail(),
                    user.get().getRole().toString(), user.get().getFullName()));
        }
    }


    public void register(RegisterRequest registerRequest) {
        User user = Mapper.getUserFromDTO(registerRequest);
        user.setPassword(bCryptPasswordEncoder.encode(registerRequest.password()));
        user.setCreatedAt(new Date(System.currentTimeMillis()));
        user.setUpdatedAt(new Date(System.currentTimeMillis()));
        user.setRole(Roles.student);
        userService.createUser(user);
    }

    @Async
    public void sendEmailToResetPassword(ResetPasswordRequest resetPasswordRequest) {
        if (userService.findUserByEmail(resetPasswordRequest.email()).isEmpty())
            throw new UserNotFound("user with this email " + resetPasswordRequest.email() + " was not found");


        String token = jwtUtils.resetPasswordToken(resetPasswordRequest.email());
        String url = "localhost:4000/auth/reset-password?token=" + token;

        userSessionService.createUserSession(resetPasswordRequest.email(), token);

        mailSender.sendResetPassword(resetPasswordRequest.email(), url);
    }

    public void updatePassword(NewPassword newPassword, String token) {

        String email = userSessionService.validCredentials(token);

        User user = userService.findUserByEmail(email).get();

        user.setPassword(bCryptPasswordEncoder.encode(newPassword.password()));

        userService.updateUser(user);

        userSessionService.updateUsed(token);

    }

    public boolean isValidToken(String token) {
        jwtUtils.validateToken(token);
        return true;
    }

    public String extractUserEmailFromToken(String token) {
        return jwtUtils.extractEmail(token.substring(7));
    }

}
