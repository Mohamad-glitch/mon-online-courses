package com.example.mononlinecourses.exception;

import io.jsonwebtoken.JwtException;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String,String>> userNotFound(UserNotFound userNotFound) {
        log.warn(userNotFound.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    @ExceptionHandler(UserPasswordOrEmailIsNotCorrect.class)
    public ResponseEntity<Map<String, String >> userPasswordOrEmailNotCorrect(UserPasswordOrEmailIsNotCorrect userPasswordOrEmailIsNotCorrect){
        log.warn(userPasswordOrEmailIsNotCorrect.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","User password or email is not correct");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String >> jwtError(JwtException e){
        log.warn(e.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","Unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        log.warn(ex.getMessage());
        Map<String, String> map = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> map.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(UserExists.class)
    public ResponseEntity<Map<String,String>> userExists(UserExists userExists) {
        log.warn(userExists.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","User exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
    }


    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<Map<String,String>> invalidCredentials(InvalidCredentials invalidCredentials) {
        log.warn(invalidCredentials.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","Invalid credentials");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }


    @ExceptionHandler(UrlHasBeenUsed.class)
    public ResponseEntity<Map<String ,String>>  urlHasBeenUsed(UrlHasBeenUsed urlHasBeenUsed){
        log.warn(urlHasBeenUsed.getMessage());
        Map<String ,String> map = new HashMap<>();
        map.put("error","url has been used");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(InstructorRoleNeeded.class)
    public ResponseEntity<Map<String, String>>  instructorRoleNeeded(InstructorRoleNeeded e) {
        log.warn(e.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error",e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
    }

    @ExceptionHandler(ImageWasNotSent.class)
    public ResponseEntity<Map<String, String>> imageWasNotSent(ImageWasNotSent imageWasNotSent) {
        log.warn(imageWasNotSent.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","image was not sent");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(TagNameCantBeEmpty.class)
    public ResponseEntity<Map<String, String>>  tagNameCantBeEmpty(TagNameCantBeEmpty e) {
        log.warn(e.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","tag name cant be empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(TagsRequiredException.class)
    public ResponseEntity<Map<String, String>> tagsRequiredException(TagsRequiredException e) {
        log.warn(e.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","tags required");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }


    @ExceptionHandler(CategoryNameCantBeEmpty.class)
    public ResponseEntity<Map<String, String>> categoryNameCantBeEmpty(CategoryNameCantBeEmpty e) {
        log.warn(e.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error","category name cant be empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(SectionHasPostionAlready.class)
    public ResponseEntity<Map<String, String>> sectionHasPostionAlready(SectionHasPostionAlready e) {
        log.warn(e.getMessage());
        Map<String,String> map = new HashMap<>();
        map.put("error",e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
}
