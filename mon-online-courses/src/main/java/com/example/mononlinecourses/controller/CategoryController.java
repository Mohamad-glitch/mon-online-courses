package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.Requests.CreateCategoryRequest;
import com.example.mononlinecourses.dto.responses.ShowCategoryResponse;
import com.example.mononlinecourses.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/create-category")
    @Operation(summary = "this method will create a category", description = "this method will check if same category exists in DB if yes it will skip if no it will save it")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "this will appear if there is nothing wrong",
                    content = @Content(schema = @Schema)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "this will appears only if there is nothing was sent or it was only a spaces request",
                    content = @Content(schema = @Schema)
            )
    })
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CreateCategoryRequest category) {
        categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/show-all-categorise")
    @Operation(summary = "this method will return all categorise", description = "this method will return all categorise")
    @ApiResponse(
            responseCode = "200",
            description = "this will appear if there is nothing wrong",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ShowCategoryResponse.class))
            )
    )
    public ResponseEntity<List<ShowCategoryResponse>> showAllCategories() {


        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

}
