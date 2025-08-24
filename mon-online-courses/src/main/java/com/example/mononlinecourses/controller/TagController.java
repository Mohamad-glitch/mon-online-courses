package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.CreateTagRequest;
import com.example.mononlinecourses.dto.ShowTagsResponse;
import com.example.mononlinecourses.service.TagService;
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

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping("/show-all-tags")
    @Operation(summary = "get all tags", description = "get all tags name in small case")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "method done successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ShowTagsResponse.class))
                    )
            )
    })
    public ResponseEntity<List<ShowTagsResponse>> showAllTags() {

        return ResponseEntity.ok().body(tagService.showAllTags());
    }

    @PostMapping("/create-tag")
    @Operation(summary = "create new tag", description = "this method wil create new tag but if it was in DB then it will skip it")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "method done successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "if user gave an empty string or only spaces it will be rejected",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(contentSchema = HashMap.class)
                    )
            )
    })
    public ResponseEntity<Void> createTag(@Valid @RequestBody CreateTagRequest createTagRequest) {

        tagService.saveTag(createTagRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/create-list-of-tags")
    @Operation(summary = "create new list of tags", description = "this method wil create new tag but if it was in DB then it will skip it")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "method done successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "if user gave an empty string or only spaces it will be rejected and stop adding the rest of the tags",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(contentSchema = HashMap.class)
                    )
            )
    })
    public ResponseEntity<Void> createListOfTags(@Valid @RequestBody List<CreateTagRequest> createTagRequest) {


        tagService.saveTags(createTagRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
