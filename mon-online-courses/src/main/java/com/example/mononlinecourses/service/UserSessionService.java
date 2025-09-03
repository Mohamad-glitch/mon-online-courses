package com.example.mononlinecourses.service;

import com.example.mononlinecourses.exception.InvalidCredentials;
import com.example.mononlinecourses.exception.UrlHasBeenUsed;
import com.example.mononlinecourses.model.UserSessions;
import com.example.mononlinecourses.repository.UserSessionDao;
import com.example.mononlinecourses.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserSessionService {

    private final UserSessionDao userSessionDao;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserSessionService(UserSessionDao userSessionDao, JwtUtils jwtUtils) {
        this.userSessionDao = userSessionDao;
        this.jwtUtils = jwtUtils;
    }


    public void createUserSession(String email, String token) {
        UserSessions userSessions = new UserSessions();

        userSessions.setEmail(email);
        userSessions.setToken(token);
        userSessions.setCreatedAt(new Date());
        userSessions.setUsed(false);

        userSessionDao.save(userSessions);
    }


    public String validCredentials(String token) {


        jwtUtils.validateToken(token);

        String email = userSessionDao.getEmailByToken(token);


        if (!email.equals(jwtUtils.extractEmail(token))) {
            throw new InvalidCredentials("Invalid credentials");
        } else if (userSessionDao.getUsedByToken(token)) {
            throw new UrlHasBeenUsed("url has been used");
        }

        return email;
    }

    @Transactional
    public void updateUsed(String token) {

        userSessionDao.updateAccessed(token);

    }
}
