package com.example.mononlinecourses.dto.responses;

import java.util.UUID;

public record ShowVideoResponse(
        UUID videoID,
        String videoTitle,
        String videoUrl,
        long videoDurationInSeconds,
        long videoPosition
) {
}
