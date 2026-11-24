package com.globallearning.lms.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.globallearning.lms.catalog.domain.Course;
import com.globallearning.lms.catalog.domain.CourseLevel;
import com.globallearning.lms.catalog.service.CourseService;
import com.globallearning.lms.catalog.service.LegacyCourseCodeParser;
import com.globallearning.lms.catalog.web.CourseController;
import com.globallearning.lms.catalog.web.mapper.CourseMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = CourseController.class,
    properties = {
        "lms.auth.username=test-user",
        "lms.auth.password=test-password",
        "lms.auth.jwt-secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
    }
)
@AutoConfigureMockMvc
@Import({CourseMapper.class, LegacyCourseCodeParser.class})
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @Test
    void listsActiveCourses() throws Exception {
        given(courseService.listActive(nullable(CourseLevel.class), nullable(String.class)))
            .willReturn(List.of(
                new Course("CS1007", "Foundations of Programming", "Basics.", CourseLevel.FOUNDATION, 10, true),
                new Course("CS2110", "Object Oriented Design", "Design.", CourseLevel.INTERMEDIATE, 15, true)
            ));

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].code").value("CS1007"))
            .andExpect(jsonPath("$[1].title").value("Object Oriented Design"));
    }

    @Test
    void returnsCourseDetailByCode() throws Exception {
        given(courseService.getByCode(any()))
            .willReturn(new Course("CS3401", "Distributed Systems", "Consensus.", CourseLevel.ADVANCED, 20, true));

        mockMvc.perform(get("/api/courses/CS3401"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("CS3401"))
            .andExpect(jsonPath("$.level").value("ADVANCED"));
    }

    @Test
    void requiresAuthenticationForCourseUpdates() throws Exception {
        mockMvc.perform(put("/api/courses/CS1007")
                .contentType("application/json")
                .content("{\"title\":\"Updated\",\"description\":\"Description\",\"credits\":10}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptsBearerAuthenticationForCourseUpdates() throws Exception {
        given(courseService.update("CS1007", "Updated", "Description", 10))
            .willReturn(new Course("CS1007", "Updated", "Description", CourseLevel.FOUNDATION, 10, true));

        mockMvc.perform(put("/api/courses/CS1007")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType("application/json")
                .content("{\"title\":\"Updated\",\"description\":\"Description\",\"credits\":10}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated"));
    }
}
