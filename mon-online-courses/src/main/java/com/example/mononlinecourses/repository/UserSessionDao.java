package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSessionDao extends JpaRepository<UserSessions, UUID> {
    @Query("select u.email from UserSessions u where u.token =:token ")
    String getEmailByToken(@Param("token") String token);


    @Query("select u.used from UserSessions u where u.token =:token ")
    boolean getUsedByToken(@Param("token") String token);

    @Modifying
    @Query("update UserSessions u set u.used = true  where u.token=:token")
    void updateAccessed(@Param("token") String  token);
}
