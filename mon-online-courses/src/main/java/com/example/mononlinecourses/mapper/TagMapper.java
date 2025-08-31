package com.example.mononlinecourses.mapper;

import com.example.mononlinecourses.dto.responses.ShowTagsResponse;
import com.example.mononlinecourses.model.Tag;

public class TagMapper {

    public static ShowTagsResponse fromTagToShowTagsResponse(Tag tag){
        return new ShowTagsResponse(tag.getName());
    }


}
