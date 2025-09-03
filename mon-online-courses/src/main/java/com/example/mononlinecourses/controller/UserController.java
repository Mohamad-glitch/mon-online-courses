package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.Requests.RequestToken;
import com.example.mononlinecourses.dto.Requests.UpdateUserInfoRequest;
import com.example.mononlinecourses.dto.responses.ShowUserResponse;
import com.example.mononlinecourses.exception.InvalidCredentials;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.service.AuthService;
import com.example.mononlinecourses.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    public void isAuthorized(String token) {
        if (!token.startsWith("Bearer ") || !authService.isValidToken(token.substring(7))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/show-user")
    @Operation(summary = "show user info", description = "show user full name, email and bio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "the request is done successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShowUserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    public ResponseEntity<ShowUserResponse> getUser(@Valid @RequestHeader("Authorization") RequestToken requestToken) {

        isAuthorized(requestToken.token());

        ShowUserResponse user = Mapper
                .getShowUserDTOFromUser(
                        userService.findUserByEmail(
                                authService.extractUserEmailFromToken(requestToken.token())
                        ).orElseThrow(() -> new InvalidCredentials("token is not correct")));

        return ResponseEntity.ok().body(user);
    }


    @PatchMapping("/update-user-info")
    @Operation(summary = "update user info", description = "user can update his full name and bio only and the name should not be empty")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "user info updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShowUserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "if user updated his name but did not give any new value it wont work it would show a message",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    public ResponseEntity<ShowUserResponse> updateUserInfo(@Valid @RequestHeader("Authorization") RequestToken requestToken
            , @Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        isAuthorized(requestToken.token());

        ShowUserResponse user = userService.updateUserInfo(
                authService.extractUserEmailFromToken(requestToken.token()), updateUserInfoRequest);

        return ResponseEntity.ok().body(user);
    }


    @PatchMapping("/become-instructor")
    @Operation(summary = "update user role", description = "update user role for student to instructor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "user info updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    public ResponseEntity<String> becomeInstructor(@Valid @RequestHeader("Authorization") RequestToken requestToken) {
        isAuthorized(requestToken.token());


        return ResponseEntity.ok().body(
                userService.updateUserRoleFromStudentToInstructor(
                        authService.extractUserEmailFromToken(requestToken.token()))
        );
    }

    /*
    TODO: create the controller for the courses and manage them then add here my courses method will return all the courses
     the user enrolled in
     */


}
