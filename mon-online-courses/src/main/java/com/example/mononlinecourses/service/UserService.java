package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.responses.ShowUserResponse;
import com.example.mononlinecourses.dto.Requests.UpdateUserInfoRequest;
import com.example.mononlinecourses.enums.Roles;
import com.example.mononlinecourses.exception.UserExists;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.model.User;
import com.example.mononlinecourses.repository.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> findUserByEmail(String email) {
        return userDao.findUsersByEmail(email);
    }

    @Transactional
    public void createUser(User user) {
        if(userDao.findUsersByEmail(user.getEmail()).isPresent()) {
            throw new UserExists("User already exists with this email: "+ user.getEmail());
        }
        userDao.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        userDao.save(user);
    }


    public ShowUserResponse updateUserInfo(String email, UpdateUserInfoRequest userInfoRequest) {

        User user = userDao.findUsersByEmail(email).get();
        user.setFullName(userInfoRequest.fullName());
        user.setBio(userInfoRequest.bio());

        user = userDao.save(user);

        return Mapper.getShowUserDTOFromUser(user);
    }

    public String updateUserRoleFromStudentToInstructor(String email){
        User user = userDao.findUsersByEmail(email).get();
        if(user.getRole() == Roles.instructor)
            return "user is already instructed";

        user.setRole(Roles.instructor);
        userDao.save(user);

        return "user is updated";
    }

    public boolean isUserInstructor(String email){
        return userDao.existsByEmailAndRole(email, Roles.instructor);
    }



}
