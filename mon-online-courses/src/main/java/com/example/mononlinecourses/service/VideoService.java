package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.Requests.CreateVideoRequest;
import com.example.mononlinecourses.dto.Requests.UpdateVideoRequest;
import com.example.mononlinecourses.dto.responses.ShowVideoResponse;
import com.example.mononlinecourses.exception.VideoNotFound;
import com.example.mononlinecourses.mapper.VideoMapper;
import com.example.mononlinecourses.model.Section;
import com.example.mononlinecourses.model.Video;
import com.example.mononlinecourses.repository.VideoDao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoDao videoDao;
    private final SectionService sectionService;
    private final FileService fileService;

    public VideoService(VideoDao videoDao, SectionService sectionService, FileService fileService) {
        this.videoDao = videoDao;
        this.sectionService = sectionService;
        this.fileService = fileService;
    }


    public List<ShowVideoResponse> getAllVideoFromSection(UUID sectionID) {

        Section section = sectionService.getSectionById(sectionID);

        return section.getVideos().stream().map(VideoMapper::fromVideoToShowVideoResponse).toList();
    }


    public void createVideo(@Valid CreateVideoRequest request, UUID sectionId, String token) {
        // this will check if this request is he authorized to make CRUD

        sectionService.isSectionOwnedByInstructor(token, sectionId);

        String videoUrl = fileService.savedVideo(request.getVideo());

        Section section = sectionService.getSectionById(sectionId);

        Video video = new Video();
        video.setTitle(request.getVideoTitle());
        video.setVideoUrl(videoUrl);
        video.setSection(section);
        video.setCreatedAt(new Date(System.currentTimeMillis()));
        video.setUpdatedAt(new Date(System.currentTimeMillis()));
        video.setDurationInSeconds(FileService.getVideoDurationInSeconds(request.getVideo()));
        video.setPosition(videoDao.findMaxPositionBySectionId(sectionId) + 1);// adding one of the max number in the DB
        videoDao.save(video);

    }

    public void updateVideoTitle(
            UpdateVideoRequest request,
            String token,
            UUID videoId
    ) {

        Video video = videoDao.findById(videoId)
                .orElseThrow(() -> new VideoNotFound("video not found"));


        sectionService.isSectionOwnedByInstructor(token, video.getSection().getId());

        video.setTitle(request.videoTitle());

        videoDao.save(video);
    }


    public void deleteVideo(String token, UUID videoId) {
        Video video = videoDao.findById(videoId).orElseThrow(() -> new VideoNotFound("video not found"));
        sectionService.isSectionOwnedByInstructor(token, video.getSection().getId());

        fileService.deleteFile(video.getVideoUrl());

        videoDao.deleteById(videoId);
    }
}
