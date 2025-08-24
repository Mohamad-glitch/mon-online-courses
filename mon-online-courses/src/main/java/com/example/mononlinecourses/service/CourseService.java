package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.CreateCourseRequest;
import com.example.mononlinecourses.dto.ShowInstructorCourses;
import com.example.mononlinecourses.exception.InstructorRoleNeeded;
import com.example.mononlinecourses.exception.TagsRequiredException;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.User;
import com.example.mononlinecourses.repository.CourseDao;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    private final Path directoryPath = Paths.get("C:\\Users\\mshlo\\OneDrive\\Desktop\\images");
    private final AuthService authService;
    private final UserService userService;
    private final CourseDao courseDao;
    private final TagService tagService;


    public CourseService(AuthService authService, UserService userService, CourseDao courseDao, TagService tagService) {
        this.authService = authService;
        this.userService = userService;
        this.courseDao = courseDao;
        this.tagService = tagService;
    }

    public List<Course> getAllCourses() {

        return courseDao.findAll();
    }


    public List<String> cleanTags(List<String> tags) {
        List<String> result = new ArrayList<>();
        for (String tag : tags) {
            String temp = tag.toLowerCase().trim();
            if (!temp.isEmpty()) {
                result.add(temp);
            }
        }

        if (result.isEmpty())
            throw new TagsRequiredException("At least one tag is required to create a course.");

        return result;
    }

    /*
    TODO : add a category for the course dynamically then show to instructor that he can add to the course sections
        this is next update   
     */
    @Transactional
    public void createCourse(CreateCourseRequest createCourseRequest,
                             MultipartFile courseImage,
                             String instructorEmail
    ) {

        /*
         in this method it will make sure that the tags are rela no empty and
          then make it to lower case then get back what is valid but if there is none it will throw an error
         */
        createCourseRequest.setTags(cleanTags(createCourseRequest.getTags()));

        if (!userService.isUserInstructor(instructorEmail)) {
            throw new InstructorRoleNeeded("you have to be Instructor to create a course");
        }

        User user = userService.findUserByEmail(instructorEmail).get();


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
        createCourse.setTags(tagService.getAddedTags(createCourseRequest.getTags()));
        createCourse.setThumbnailUrl(targetPath.toString());


        courseDao.save(createCourse);
    }

    public List<ShowInstructorCourses> getAllInstructorCourses(String instructorEmail) {


        return courseDao.getCoursesByInstructor_Email(instructorEmail).stream()
                .map(Mapper::showInstructorCoursesFromCourse).toList();
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
