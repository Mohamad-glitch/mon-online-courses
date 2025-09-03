package com.example.mononlinecourses.utils;

import com.example.mononlinecourses.exception.NotValidUUID;

import java.util.UUID;

public class UUIDValidator {

    public static UUID validateUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new NotValidUUID(e.getMessage());
        }
    }


}
