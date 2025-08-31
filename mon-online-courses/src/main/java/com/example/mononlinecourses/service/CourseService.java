package com.example.mononlinecourses.service;

import com.example.mononlinecourses.dto.Requests.CreateCourseRequest;
import com.example.mononlinecourses.dto.responses.PagedResponse;
import com.example.mononlinecourses.dto.responses.ShowCoursesResponse;
import com.example.mononlinecourses.dto.responses.ShowInstructorCourses;
import com.example.mononlinecourses.exception.CategoryNameCantBeEmpty;
import com.example.mononlinecourses.exception.InstructorRoleNeeded;
import com.example.mononlinecourses.exception.TagsRequiredException;
import com.example.mononlinecourses.mapper.Mapper;
import com.example.mononlinecourses.model.Course;
import com.example.mononlinecourses.model.User;
import com.example.mononlinecourses.repository.CourseDao;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    private final FileService fileService;
    private final AuthService authService;
    private final UserService userService;
    private final CourseDao courseDao;
    private final TagService tagService;
    private final CategoryService categoryService;


    public CourseService(FileService fileService, AuthService authService, UserService userService, CourseDao courseDao, TagService tagService, CategoryService categoryService) {
        this.fileService = fileService;
        this.authService = authService;
        this.userService = userService;
        this.courseDao = courseDao;
        this.tagService = tagService;
        this.categoryService = categoryService;
    }

    public PagedResponse<ShowCoursesResponse> getAllCourses(int pageNumber, int pageSize) {

        Page page = courseDao.findAll(PageRequest.of(pageNumber, pageSize)).map(Mapper::showCoursesResponseFromCourse);

        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }


    public List<String> cleanTags(List<String> tags) {
        List<String> result = tags.stream()
                .map(tag -> tag.toLowerCase().trim())
                .filter(tag -> !tag.isEmpty())
                .toList();

        if (result.isEmpty()) {
            throw new TagsRequiredException("At least one tag is required to create a course.");
        }
        return result;
    }

    public List<String> cleanCategory(List<String> categorise) {
        List<String> result = categorise.stream()
                .map(category -> category.toLowerCase().trim())
                .filter(category -> !category.isEmpty())
                .toList();


        if (result.isEmpty())
            throw new CategoryNameCantBeEmpty("Category name cannot be empty.");


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
          cleanCategory(createCourseRequest.getCategory())
         */
        createCourseRequest.setTags(cleanTags(createCourseRequest.getTags()));
        createCourseRequest.setCategory(cleanCategory(createCourseRequest.getCategory()));

        if (!userService.isUserInstructor(instructorEmail))
            throw new InstructorRoleNeeded("you have to be Instructor to create a course");


        User user = userService.findUserByEmail(instructorEmail).get();


        Course createCourse = Mapper.getCourseFromCreateCourseRequest(createCourseRequest);
        createCourse.setDurationMinutes(0);
        createCourse.setCreatedAt(new Date(System.currentTimeMillis()));
        createCourse.setUpdatedAt(new Date(System.currentTimeMillis()));
        createCourse.setInstructor(user);
        createCourse.setTags(tagService.getAddedTags(createCourseRequest.getTags()));
        createCourse.setThumbnailUrl(fileService.savedImage(courseImage));
        createCourse.setCategorises(categoryService.getAddedCategorise(createCourseRequest.getCategory()));


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

    public Course getCourseById(UUID id) {
        return courseDao.findById(id).get();
    }
}
