package com.example.mononlinecourses.controller;

import com.example.mononlinecourses.dto.Requests.CreateVideoRequest;
import com.example.mononlinecourses.dto.Requests.UpdateVideoRequest;
import com.example.mononlinecourses.dto.responses.ShowVideoResponse;
import com.example.mononlinecourses.service.AuthService;
import com.example.mononlinecourses.service.VideoService;
import com.example.mononlinecourses.utils.UUIDValidator;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;
    private final AuthService authService;

    public VideoController(VideoService videoService, AuthService authService) {
        this.videoService = videoService;
        this.authService = authService;
    }

    public void isAuthorized(String token) {
        if (!token.startsWith("Bearer ") || !authService.isValidToken(token.substring(7))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get-all-section-content/{sectionID}")
    @Operation(summary = "show all videos", description = "this method will show video content")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "every thing works as intended to do",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ShowVideoResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "if the id was sent was not a UUID this will be printed or section/video not found",
                    content = @Content(schema = @Schema(implementation = Void.class))
            )
    })
    public ResponseEntity<List<ShowVideoResponse>> getAllVideosFroTheSameSection(@PathVariable String sectionID) {


        UUID sectionUUID = UUIDValidator.validateUUID(sectionID);


        return ResponseEntity.ok().body(videoService.getAllVideoFromSection(sectionUUID));
    }


    @PostMapping("/create-video/{sectionId}")
    @Operation(summary = "create new video", description = "this will create a new video")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "created successfully",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "if the id was not in uuid format or section was not found",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "not authorized to create course",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "section was not found",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )

    })
    public ResponseEntity<Void> createVideo(
            @RequestHeader("Authorization") String token,
            @Valid @ModelAttribute CreateVideoRequest request,
            @PathVariable String sectionId
    ) {
        isAuthorized(token);


        UUID sectionUUID = UUIDValidator.validateUUID(sectionId);

        videoService.createVideo(request, sectionUUID, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PatchMapping("/update-video/{videoId}")
    @Operation(summary = "update video title", description = "update video title and it should not be empty")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "updated successfully",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "title is empty",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = " section id not correct",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "not authorized to make update",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "section id not found",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    public ResponseEntity<Void> updateVideo(
            @RequestHeader("Authorization") String token,
            @PathVariable String videoId,
            @Valid @RequestBody UpdateVideoRequest request
    ) {
        isAuthorized(token);
        UUID videoUUID = UUIDValidator.validateUUID(videoId);

        videoService.updateVideoTitle(request, token, videoUUID);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/delete-video/{videoId}")
    @Operation(summary = "delete video", description = "delete video via video id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "deleted successfully",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "video id was not correct or video not found",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "not authorize to delete video",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    public ResponseEntity<Void> deleteVideo(
            @RequestHeader("Authorization") String token,
            @PathVariable String videoId
    ) {
        isAuthorized(token);
        UUID videoUUID = UUIDValidator.validateUUID(videoId);

        videoService.deleteVideo(token, videoUUID);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
