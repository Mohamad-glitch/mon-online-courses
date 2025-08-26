package com.example.mononlinecourses.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileService {

    private final Path directoryPath = Paths.get("C:\\Users\\mshlo\\OneDrive\\Desktop\\images");

    public String savedImage(MultipartFile file) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String newFileName = "img_" + timestamp + ".jpg";

        Path targetPath = directoryPath.resolve(newFileName);

        try {
            // Save the file to the directory
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save course image", e);
        }

        return targetPath.toString();
    }
}
