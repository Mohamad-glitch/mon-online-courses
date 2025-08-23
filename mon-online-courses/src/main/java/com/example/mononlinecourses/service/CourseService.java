package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.CreateCourseRequest;
import com.example.mononlinecourses.dto.ShowInstructorCourses;
import com.example.mononlinecourses.exception.InstructorRoleNeeded;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.Tag;
import com.example.mononlinecourses.model.User;
import com.example.mononlinecourses.repository.CourseDao;
import com.example.mononlinecourses.repository.TagDao;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    private final Path directoryPath = Paths.get("C:\\Users\\mshlo\\OneDrive\\Desktop\\images");
    private final AuthService authService;
    private final UserService userService;
    private final TagDao tagDao;
    private final CourseDao courseDao;


    public CourseService(AuthService authService, UserService userService, TagDao tagDao, CourseDao courseDao) {
        this.authService = authService;
        this.userService = userService;
        this.tagDao = tagDao;
        this.courseDao = courseDao;
    }

    public List<Course> getAllCourses() {

        return courseDao.findAll();
    }

    /*
    this method will take the user input to create a course so it will take like this
    title, description, price, image
    then save the image to this directoryPath and rename the file to img_ current date_hour,mint,seconds, ms then make the image with
    jpg

    TODO: still need to add to it dynamic inputs for category and tags

     */
    @Transactional
    public void createCourse(CreateCourseRequest createCourseRequest,
                             MultipartFile courseImage,
                             String instructorEmail
    ) {

        if (!userService.isUserInstructor(instructorEmail)) {
            throw new InstructorRoleNeeded("you have to be Instructor to create a course");
        }

        User user = userService.findUserByEmail(instructorEmail).get();

        Tag tag = tagDao.findTagsByName("IT");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String newFileName = "img_" + timestamp + ".jpg";

        Path targetPath = directoryPath.resolve(newFileName);

        try {
            // Save the file to the directory
            Files.copy(courseImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save course image", e);
        }


        Course createCourse = Mapper.getCourseFromCreateCourseRequest(createCourseRequest);
        createCourse.setDurationMinutes(0);
        createCourse.setCreatedAt(new Date(System.currentTimeMillis()));
        createCourse.setUpdatedAt(new Date(System.currentTimeMillis()));
        createCourse.setInstructor(user);
        createCourse.setTags(Collections.singletonList(tag));
        createCourse.setThumbnailUrl(targetPath.toString());


        courseDao.save(createCourse);


    }

    public List<ShowInstructorCourses> getAllInstructorCourses(String instructorEmail) {


        return courseDao.getCoursesByInstructor_Email(instructorEmail).stream()
                .map(x ->
                        new ShowInstructorCourses(x.getId(), x.getTitle(), x.getDescription(), x.getPrice(), x.getDurationMinutes(), x.getLanguage())
                ).toList();
    }

    public Resource getCourseImage(UUID id) {
        String thumbnailUrl = courseDao.findCoursesById(id).getThumbnailUrl();
        if (thumbnailUrl == null) return null;  // No image stored

        Path imagePath = Paths.get(thumbnailUrl);

        try {

            return new UrlResource(imagePath.toUri());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e.getMessage());
            return null;
        }
    }
}
