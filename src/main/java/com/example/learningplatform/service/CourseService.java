package com.example.learningplatform.service;

import com.example.learningplatform.dto.CourseDTO;
import com.example.learningplatform.dto.CreateCourseRequest;
import com.example.learningplatform.dto.LessonDTO;
import com.example.learningplatform.dto.ModuleDTO;
import com.example.learningplatform.entity.*;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ModuleRepository moduleRepository;

    public CourseDTO createCourse(CreateCourseRequest request) {
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.getTeacherId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setDuration(request.getDuration());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setPrice(request.getPrice());
        course.setCategory(category);
        course.setTeacher(teacher);
        course.setIsPublished(false);

        // Добавление тегов
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                course.getTags().add(tag);
            }
        }

        Course savedCourse = courseRepository.save(course);
        log.info("Created course with id: {}", savedCourse.getId());
        return convertToDTO(savedCourse);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return convertToDTO(course);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseWithModules(Long id) {
        Course course = courseRepository.findByIdWithModules(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return convertToDetailedDTO(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getPublishedCourses() {
        return courseRepository.findByIsPublishedTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> searchCourses(String query) {
        return courseRepository.searchCourses(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO updateCourse(Long id, CreateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setDuration(request.getDuration());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());
        course.setPrice(request.getPrice());

        if (!course.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("Updated course with id: {}", id);
        return convertToDTO(updatedCourse);
    }

    public void publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsPublished(true);
        courseRepository.save(course);
        log.info("Published course with id: {}", id);
    }

    public void unpublishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsPublished(false);
        courseRepository.save(course);
        log.info("Unpublished course with id: {}", id);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
        log.info("Deleted course with id: {}", id);
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setDuration(course.getDuration());
        dto.setStartDate(course.getStartDate());
        dto.setEndDate(course.getEndDate());
        dto.setPrice(course.getPrice());
        dto.setIsPublished(course.getIsPublished());
        dto.setCategoryId(course.getCategory().getId());
        dto.setCategoryName(course.getCategory().getName());
        dto.setTeacherId(course.getTeacher().getId());
        dto.setTeacherName(course.getTeacher().getName());
        dto.setTags(course.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        return dto;
    }

    private CourseDTO convertToDetailedDTO(Course course) {
        CourseDTO dto = convertToDTO(course);

        // Добавляем модули с уроками
        if (course.getModules() != null) {
            dto.setModules(course.getModules().stream().map(module -> {
                ModuleDTO moduleDTO = new ModuleDTO();
                moduleDTO.setId(module.getId());
                moduleDTO.setTitle(module.getTitle());
                moduleDTO.setDescription(module.getDescription());
                moduleDTO.setOrderIndex(module.getOrderIndex());
                moduleDTO.setCourseId(module.getCourse().getId());

                if (module.getLessons() != null) {
                    moduleDTO.setLessons(module.getLessons().stream().map(lesson -> {
                        LessonDTO lessonDTO = new LessonDTO();
                        lessonDTO.setId(lesson.getId());
                        lessonDTO.setTitle(lesson.getTitle());
                        lessonDTO.setContent(lesson.getContent());
                        lessonDTO.setVideoUrl(lesson.getVideoUrl());
                        lessonDTO.setOrderIndex(lesson.getOrderIndex());
                        lessonDTO.setModuleId(lesson.getModule().getId());
                        return lessonDTO;
                    }).collect(Collectors.toList()));
                }

                return moduleDTO;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}