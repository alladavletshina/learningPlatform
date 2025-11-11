package com.example.learningplatform.service;

import com.example.learningplatform.dto.CourseDTO;
import com.example.learningplatform.dto.CreateCourseRequest;
import com.example.learningplatform.dto.CreateLessonRequest;
import com.example.learningplatform.dto.CreateModuleRequest;
import com.example.learningplatform.dto.LessonDTO;
import com.example.learningplatform.dto.ModuleDTO;
import com.example.learningplatform.entity.Category;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.Tag;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.exception.ResourceNotFoundException;
import com.example.learningplatform.repository.CategoryRepository;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.LessonRepository;
import com.example.learningplatform.repository.ModuleRepository;
import com.example.learningplatform.repository.TagRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final LessonRepository lessonRepository;

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
    public CourseDTO getCourseFullStructure(Long id) {
        Course course = courseRepository.findByIdWithModulesAndTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        List<Module> modulesWithLessons = moduleRepository.findByCourseIdWithLessons(id);
        course.setModules(modulesWithLessons);

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

        if (request.getTags() != null) {
            course.getTags().clear();
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

    public ModuleDTO addModuleToCourse(CreateModuleRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        List<Module> existingModules = moduleRepository.findByCourseId(request.getCourseId());

        boolean orderExists = existingModules.stream()
                .anyMatch(module -> module.getOrderIndex().equals(request.getOrderIndex()));
        if (orderExists) {
            throw new IllegalArgumentException("Module with order index " + request.getOrderIndex() + " already exists in this course");
        }

        Module module = new Module();
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setCourse(course);
        module.setOrderIndex(request.getOrderIndex());

        Module savedModule = moduleRepository.save(module);
        log.info("Added module {} to course {}", savedModule.getId(), course.getId());

        return convertToModuleDTO(savedModule);
    }

    public LessonDTO addLessonToModule(CreateLessonRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + request.getModuleId()));

        List<Lesson> existingLessons = lessonRepository.findByModuleId(request.getModuleId());

        boolean orderExists = existingLessons.stream()
                .anyMatch(lesson -> lesson.getOrderIndex().equals(request.getOrderIndex()));
        if (orderExists) {
            throw new IllegalArgumentException("Lesson with order index " + request.getOrderIndex() + " already exists in this module");
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setModule(module);
        lesson.setOrderIndex(request.getOrderIndex());

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Added lesson {} to module {}", savedLesson.getId(), module.getId());

        return convertToLessonDTO(savedLesson);
    }

    public void updateModuleOrder(Long moduleId, Integer newOrder) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        Course course = module.getCourse();

        List<Module> courseModules = moduleRepository.findByCourseId(course.getId());

        Optional<Module> conflictingModule = courseModules.stream()
                .filter(m -> !m.getId().equals(moduleId) && m.getOrderIndex().equals(newOrder))
                .findFirst();

        if (conflictingModule.isPresent()) {
            throw new IllegalArgumentException("Another module already has order index: " + newOrder);
        }

        module.setOrderIndex(newOrder);
        moduleRepository.save(module);
        log.info("Updated module {} order to {}", moduleId, newOrder);
    }

    public void updateLessonOrder(Long lessonId, Integer newOrder) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Module module = lesson.getModule();

        List<Lesson> moduleLessons = lessonRepository.findByModuleId(module.getId());

        Optional<Lesson> conflictingLesson = moduleLessons.stream()
                .filter(l -> !l.getId().equals(lessonId) && l.getOrderIndex().equals(newOrder))
                .findFirst();

        if (conflictingLesson.isPresent()) {
            throw new IllegalArgumentException("Another lesson already has order index: " + newOrder);
        }

        lesson.setOrderIndex(newOrder);
        lessonRepository.save(lesson);
        log.info("Updated lesson {} order to {}", lessonId, newOrder);
    }

    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        List<Lesson> moduleLessons = lessonRepository.findByModuleId(moduleId);

        if (!moduleLessons.isEmpty()) {
            throw new IllegalStateException("Cannot delete module with existing lessons. Delete lessons first.");
        }

        moduleRepository.delete(module);
        log.info("Deleted module with id: {}", moduleId);
    }

    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        lessonRepository.delete(lesson);
        log.info("Deleted lesson with id: {}", lessonId);
    }

    public ModuleDTO updateModule(Long moduleId, CreateModuleRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        if (!module.getCourse().getId().equals(request.getCourseId())) {
            throw new IllegalArgumentException("Module does not belong to the specified course");
        }

        List<Module> courseModules = moduleRepository.findByCourseId(request.getCourseId());

        if (!module.getOrderIndex().equals(request.getOrderIndex())) {
            boolean orderExists = courseModules.stream()
                    .filter(m -> !m.getId().equals(moduleId))
                    .anyMatch(m -> m.getOrderIndex().equals(request.getOrderIndex()));
            if (orderExists) {
                throw new IllegalArgumentException("Module with order index " + request.getOrderIndex() + " already exists in this course");
            }
        }

        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setOrderIndex(request.getOrderIndex());

        Module updatedModule = moduleRepository.save(module);
        log.info("Updated module with id: {}", moduleId);

        return convertToModuleDTO(updatedModule);
    }

    public LessonDTO updateLesson(Long lessonId, CreateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        if (!lesson.getModule().getId().equals(request.getModuleId())) {
            throw new IllegalArgumentException("Lesson does not belong to the specified module");
        }

        List<Lesson> moduleLessons = lessonRepository.findByModuleId(request.getModuleId());

        if (!lesson.getOrderIndex().equals(request.getOrderIndex())) {
            boolean orderExists = moduleLessons.stream()
                    .filter(l -> !l.getId().equals(lessonId))
                    .anyMatch(l -> l.getOrderIndex().equals(request.getOrderIndex()));
            if (orderExists) {
                throw new IllegalArgumentException("Lesson with order index " + request.getOrderIndex() + " already exists in this module");
            }
        }

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setOrderIndex(request.getOrderIndex());

        Lesson updatedLesson = lessonRepository.save(lesson);
        log.info("Updated lesson with id: {}", lessonId);

        return convertToLessonDTO(updatedLesson);
    }

    @Transactional(readOnly = true)
    public ModuleDTO getModuleWithLessons(Long moduleId) {
        Module module = moduleRepository.findByIdWithLessons(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        ModuleDTO dto = convertToModuleDTO(module);

        if (module.getLessons() != null) {
            dto.setLessons(module.getLessons().stream()
                    .map(this::convertToLessonDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setLessons(new ArrayList<>());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public LessonDTO getLessonWithAssignments(Long lessonId) {
        Lesson lesson = lessonRepository.findByIdWithAssignments(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        return convertToLessonDTO(lesson);
    }

    @Transactional(readOnly = true)
    public List<ModuleDTO> getCourseModules(Long courseId) {
        return moduleRepository.findByCourseId(courseId).stream()
                .map(this::convertToModuleDTO)
                .collect(Collectors.toList());
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

        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }

        dto.setTeacherId(course.getTeacher().getId());
        dto.setTeacherName(course.getTeacher().getName());
        dto.setTags(course.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        return dto;
    }

    private CourseDTO convertToDetailedDTO(Course course) {
        CourseDTO dto = convertToDTO(course);

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
                } else {
                    moduleDTO.setLessons(new ArrayList<>());
                }

                return moduleDTO;
            }).collect(Collectors.toList()));
        } else {
            dto.setModules(new ArrayList<>());
        }

        return dto;
    }

    private ModuleDTO convertToModuleDTO(Module module) {
        ModuleDTO dto = new ModuleDTO();
        dto.setId(module.getId());
        dto.setTitle(module.getTitle());
        dto.setDescription(module.getDescription());
        dto.setOrderIndex(module.getOrderIndex());
        dto.setCourseId(module.getCourse().getId());
        return dto;
    }

    private LessonDTO convertToLessonDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setContent(lesson.getContent());
        dto.setVideoUrl(lesson.getVideoUrl());
        dto.setOrderIndex(lesson.getOrderIndex());
        dto.setModuleId(lesson.getModule().getId());
        return dto;
    }
}