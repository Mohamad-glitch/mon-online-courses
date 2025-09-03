package com.example.mononlinecourses.mapper;

import com.example.mononlinecourses.dto.responses.ShowVideoResponse;
import com.example.mononlinecourses.model.Video;

public class VideoMapper {

    public static ShowVideoResponse fromVideoToShowVideoResponse(Video video){
        return new ShowVideoResponse(
                video.getId(),
                video.getTitle(),
                video.getVideoUrl(),
                video.getDurationInSeconds(),
                video.getPosition()
        );
    }


}
