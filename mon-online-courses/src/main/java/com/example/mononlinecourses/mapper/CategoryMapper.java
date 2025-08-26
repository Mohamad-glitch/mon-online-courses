package com.example.mononlinecourses.mapper;

import com.example.mononlinecourses.dto.ShowCategoryResponse;
import com.example.mononlinecourses.model.Categorise;

public class CategoryMapper {

    public static Categorise fromCategoryRequestToCategory(String createCategoryRequest) {
        Categorise categorise = new Categorise();
        categorise.setName(createCategoryRequest);
        return categorise;
    }

    public static ShowCategoryResponse fromCategoryToShowCategoryResponse(Categorise categorise){
        return new ShowCategoryResponse(categorise.getName());
    }

}
