package com.example.mononlinecourses.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileService {

    private final Path imageDirectoryPath;
    private final Path videoDirectoryPath;

    public FileService(@Value("${image.path}") String imageFilePath, @Value("${video.path}") String videoFilePath) {
        this.imageDirectoryPath = Paths.get(imageFilePath);
        this.videoDirectoryPath = Paths.get(videoFilePath);
    }


    public static long getVideoDurationInSeconds(MultipartFile file) {
        try {
            org.apache.tika.parser.AutoDetectParser parser = new org.apache.tika.parser.AutoDetectParser();
            Metadata metadata = new Metadata();
            parser.parse(file.getInputStream(), new org.xml.sax.helpers.DefaultHandler(), metadata);

            String durationStr = metadata.get("xmpDM:duration"); // duration in milliseconds
            if (durationStr != null) {
                double durationMs = Double.parseDouble(durationStr);
                return (long) durationMs; // convert to seconds
            } else {
                throw new IllegalArgumentException("Cannot read video duration");
            }
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public String savedImage(MultipartFile file) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String newFileName = "img_" + timestamp + ".jpg";

        Path targetPath = imageDirectoryPath.resolve(newFileName);

        try {
            // Save the file to the directory
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save course image", e);
        }

        return targetPath.toString();
    }

    public String savedVideo(MultipartFile file) {
        try {
            // Ensure base directory exists
            Path baseDir = videoDirectoryPath.toAbsolutePath().normalize();
            Files.createDirectories(baseDir);

            // Generate safe filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            String uuid = UUID.randomUUID().toString();
            String extension = ".mp4";

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String safeName = originalFilename.replace("\\", "/");
                safeName = safeName.substring(safeName.lastIndexOf("/") + 1);
                if (safeName.contains(".")) {
                    extension = safeName.substring(safeName.lastIndexOf("."));
                }
            }

            String newFileName = "vid_" + timestamp + "_" + uuid + extension;

            // Resolve and normalize path
            Path targetPath = baseDir.resolve(newFileName).normalize();

            // Containment check
            if (!targetPath.startsWith(baseDir)) {
                throw new SecurityException("Invalid file path");
            }

            // Save file content
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save course video", e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            // Build absolute path from directory + file name
            Path filePath = videoDirectoryPath.resolve(fileUrl).normalize();

            // Security check: prevent directory traversal
            if (!filePath.startsWith(videoDirectoryPath)) {
                throw new SecurityException("Invalid file path");
            }

            File file = filePath.toFile();
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Video deleted successfully: " + file.getAbsolutePath());
                } else {
                    throw new RuntimeException("Failed to delete video: " + file.getAbsolutePath());
                }
            } else {
                throw new RuntimeException("Video file not found: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting video: " + e.getMessage());
        }

    }
}


