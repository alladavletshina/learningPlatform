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
    public CourseDTO getCourseFullStructure(Long id) {
        Course course = courseRepository.findByIdWithModulesAndTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        // Явно загружаем уроки для каждого модуля
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

        // Обновление тегов
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

    // ========== УПРАВЛЕНИЕ СТРУКТУРОЙ КУРСА ==========

    public ModuleDTO addModuleToCourse(CreateModuleRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // Загружаем модули курса для проверки дублирования orderIndex
        List<Module> existingModules = moduleRepository.findByCourseId(request.getCourseId());

        // Проверяем уникальность orderIndex в рамках курса
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

        // Загружаем уроки модуля для проверки дублирования orderIndex
        List<Lesson> existingLessons = lessonRepository.findByModuleId(request.getModuleId());

        // Проверяем уникальность orderIndex в рамках модуля
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

        // Загружаем все модули курса для проверки
        List<Module> courseModules = moduleRepository.findByCourseId(course.getId());

        // Проверяем конфликты порядка
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

        // Загружаем все уроки модуля для проверки
        List<Lesson> moduleLessons = lessonRepository.findByModuleId(module.getId());

        // Проверяем конфликты порядка
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

        // Загружаем уроки модуля для проверки
        List<Lesson> moduleLessons = lessonRepository.findByModuleId(moduleId);

        // Проверяем, есть ли связанные уроки
        if (!moduleLessons.isEmpty()) {
            throw new IllegalStateException("Cannot delete module with existing lessons. Delete lessons first.");
        }

        moduleRepository.delete(module);
        log.info("Deleted module with id: {}", moduleId);
    }

    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        // TODO: Проверить, есть ли связанные задания (нужно добавить AssignmentRepository)

        lessonRepository.delete(lesson);
        log.info("Deleted lesson with id: {}", lessonId);
    }

    public ModuleDTO updateModule(Long moduleId, CreateModuleRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        // Проверяем, что курс совпадает
        if (!module.getCourse().getId().equals(request.getCourseId())) {
            throw new IllegalArgumentException("Module does not belong to the specified course");
        }

        // Загружаем модули курса для проверки
        List<Module> courseModules = moduleRepository.findByCourseId(request.getCourseId());

        // Проверяем уникальность orderIndex если он изменился
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

        // Проверяем, что модуль совпадает
        if (!lesson.getModule().getId().equals(request.getModuleId())) {
            throw new IllegalArgumentException("Lesson does not belong to the specified module");
        }

        // Загружаем уроки модуля для проверки
        List<Lesson> moduleLessons = lessonRepository.findByModuleId(request.getModuleId());

        // Проверяем уникальность orderIndex если он изменился
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

        // Уроки уже должны быть загружены через JOIN FETCH в репозитории
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

        LessonDTO dto = convertToLessonDTO(lesson);

        // Здесь можно добавить assignments если нужно
        // dto.setAssignments(...);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<ModuleDTO> getCourseModules(Long courseId) {
        return moduleRepository.findByCourseId(courseId).stream()
                .map(this::convertToModuleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> getModuleLessons(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId).stream()
                .map(this::convertToLessonDTO)
                .collect(Collectors.toList());
    }

    public void reorderModules(Long courseId, List<Long> moduleIdsInOrder) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Проверяем, что все модули принадлежат курсу
        for (int i = 0; i < moduleIdsInOrder.size(); i++) {
            Long moduleId = moduleIdsInOrder.get(i);
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

            if (!module.getCourse().getId().equals(courseId)) {
                throw new IllegalArgumentException("Module " + moduleId + " does not belong to course " + courseId);
            }

            module.setOrderIndex(i + 1);
            moduleRepository.save(module);
        }

        log.info("Reordered modules for course {}", courseId);
    }

    public void reorderLessons(Long moduleId, List<Long> lessonIdsInOrder) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        // Проверяем, что все уроки принадлежат модулю
        for (int i = 0; i < lessonIdsInOrder.size(); i++) {
            Long lessonId = lessonIdsInOrder.get(i);
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

            if (!lesson.getModule().getId().equals(moduleId)) {
                throw new IllegalArgumentException("Lesson " + lessonId + " does not belong to module " + moduleId);
            }

            lesson.setOrderIndex(i + 1);
            lessonRepository.save(lesson);
        }

        log.info("Reordered lessons for module {}", moduleId);
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

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

        // Безопасная обработка категории
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