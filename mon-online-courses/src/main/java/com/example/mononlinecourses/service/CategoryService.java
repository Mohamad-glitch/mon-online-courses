package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.Requests.CreateCategoryRequest;
import com.example.mononlinecourses.dto.responses.ShowCategoryResponse;
import com.example.mononlinecourses.exception.CategoryNameCantBeEmpty;
import com.example.mononlinecourses.mapper.CategoryMapper;
import com.example.mononlinecourses.model.Categorise;
import com.example.mononlinecourses.repository.CategoriseDao;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoriseDao categoriseDao;

    public CategoryService(CategoriseDao categoriseDao) {
        this.categoriseDao = categoriseDao;
    }


    @Transactional
    public void createCategory(CreateCategoryRequest categoryRequest) {
        String category = categoryRequest.name().toLowerCase().trim();

        if (category.isEmpty())
            throw new CategoryNameCantBeEmpty("category cant be empty");


        if (!categoriseDao.existsCategoriseByName(category))
            categoriseDao.save(CategoryMapper.fromCategoryRequestToCategory(category));
    }


    public List<ShowCategoryResponse> getAllCategories() {
        return categoriseDao.findAll()
                .stream().map(CategoryMapper::fromCategoryToShowCategoryResponse).toList();
    }


    public List<Categorise> getAddedCategorise(List<String> categories) {
        return categories.stream().map(categoriseDao::findCategoryByName).toList();
    }
}
