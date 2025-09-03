package com.example.mononlinecourses.mapper;

import com.example.mononlinecourses.dto.Requests.CreateSectionRequest;
import com.example.mononlinecourses.dto.responses.ShowCourseSection;
import com.example.mononlinecourses.model.Section;

import java.util.Date;

public class SectionMapper {

    public static Section fromCreateSectionRequestToSection(CreateSectionRequest createSectionRequest) {
        Section section = new Section();

        section.setTitle(createSectionRequest.sectionTitle());
        section.setCreatedAt(new Date(System.currentTimeMillis()));
        section.setUpdatedAt(new Date(System.currentTimeMillis()));
        return section;
    }


    public static ShowCourseSection fromSectionToShowCourseSection(Section section) {
        return new ShowCourseSection(
               section.getId(), section.getTitle(), section.getPosition()
        );
    }

}
