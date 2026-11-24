package com.globallearning.lms.catalog.service;

import com.globallearning.lms.catalog.audit.CatalogAuditLogger;
import com.globallearning.lms.catalog.domain.Course;
import com.globallearning.lms.catalog.domain.CourseLevel;
import com.globallearning.lms.catalog.repository.CourseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CatalogAuditLogger auditLogger;

    public CourseService(CourseRepository courseRepository, CatalogAuditLogger auditLogger) {
        this.courseRepository = courseRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional(readOnly = true)
    public List<Course> listActive(CourseLevel level, String keyword) {
        List<Course> results;
        if (keyword != null && !keyword.isBlank()) {
            results = courseRepository.searchByKeyword(keyword.trim());
        } else if (level != null) {
            results = courseRepository.findByActiveTrueAndLevelOrderByCodeAsc(level);
        } else {
            results = courseRepository.findByActiveTrueOrderByCodeAsc();
        }
        auditLogger.recordCatalogRead("anonymous", results.size());
        return results;
    }

    @Transactional(readOnly = true)
    public Course getByCode(String code) {
        return courseRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new CourseNotFoundException(code));
    }

    @Transactional
    public Course update(String code, String title, String description, Integer credits) {
        Course course = getByCode(code);
        course.setTitle(title);
        course.setDescription(description);
        course.setCredits(credits);
        return courseRepository.save(course);
    }

    // TODO(platform-team): once EnrolmentService lands, the catalogue should refuse to
    // deactivate a course that still has open enrolments or an active waitlist.
    @Transactional
    public Course deactivate(String code) {
        Course course = getByCode(code);
        course.setActive(false);
        return courseRepository.save(course);
    }
}
