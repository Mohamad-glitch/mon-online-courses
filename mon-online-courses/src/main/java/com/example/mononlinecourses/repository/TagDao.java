package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagDao extends JpaRepository<Tag, UUID> {
    Tag findTagsByName(String name);
}
