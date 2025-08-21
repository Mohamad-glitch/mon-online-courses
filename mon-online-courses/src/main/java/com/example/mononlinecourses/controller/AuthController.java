package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.*;
import com.example.mononlinecourses.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    @Operation(summary = "user login",
            description = "take user email and password if they are correct it will return JWT else error")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "will return JWT",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "error : Invalid email or password",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "error : User not found",
                    content = @Content
            )
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {


        return ResponseEntity.ok().body(authService.login(loginRequest));
    }


    @PostMapping("/register")
    @Operation(summary = "create new user", description = "it will take the user info and save it")
    @ApiResponse(
            responseCode = "201",
            description = "will return nothing"
    )
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {

        authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reset-password")
    @Operation(summary = "reset password", description = "this method will send an email to the user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "wont return anything",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "user not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> resetPasswordEmail(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        authService.sendEmailToResetPassword(resetPasswordRequest);

        return ResponseEntity.ok().build();
    }


    @PatchMapping("/reset-password")
    @Operation(summary = "update user password",
            description = "will check if the token is valid or not and then update the password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "updated successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "token has been used",
                    content = @Content
            )
    })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody NewPassword newPassword,
                                              @RequestParam String token) {


        authService.updatePassword(newPassword, token);


        return ResponseEntity.ok().build();
    }


}
