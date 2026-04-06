package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseCategoryServiceTest {

    @Mock
    private CourseCategoryRepository courseCategoryRepository;

    @InjectMocks
    private CourseCategoryService courseCategoryService;

    private CourseCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new CourseCategory("Erwachsene", 0);
    }

    @Test
    void findAll_returnsAllCategories() {
        when(courseCategoryRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(sampleCategory));

        List<CourseCategoryResponse> result = courseCategoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Erwachsene");
    }

    @Test
    void findById_existingId_returnsCategory() {
        when(courseCategoryRepository.findWithCoursesById(1L)).thenReturn(Optional.of(sampleCategory));

        CourseCategoryResponse result = courseCategoryService.findById(1L);

        assertThat(result.name()).isEqualTo("Erwachsene");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(courseCategoryRepository.findWithCoursesById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseCategoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_validRequest_savesCategory() {
        CourseCategoryRequest request = new CourseCategoryRequest("Jugendliche", 1);
        when(courseCategoryRepository.save(any(CourseCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseCategoryResponse result = courseCategoryService.create(request);

        assertThat(result.name()).isEqualTo("Jugendliche");
        assertThat(result.displayOrder()).isEqualTo(1);
        verify(courseCategoryRepository).save(any(CourseCategory.class));
    }

    @Test
    void update_existingId_updatesCategory() {
        when(courseCategoryRepository.findWithCoursesById(1L)).thenReturn(Optional.of(sampleCategory));
        when(courseCategoryRepository.save(any(CourseCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseCategoryRequest request = new CourseCategoryRequest("Senioren", 2);
        CourseCategoryResponse result = courseCategoryService.update(1L, request);

        assertThat(result.name()).isEqualTo("Senioren");
        assertThat(result.displayOrder()).isEqualTo(2);
    }

    @Test
    void delete_existingId_deletesCategory() {
        when(courseCategoryRepository.findWithCoursesById(1L)).thenReturn(Optional.of(sampleCategory));

        courseCategoryService.delete(1L);

        verify(courseCategoryRepository).delete(sampleCategory);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(courseCategoryRepository.findWithCoursesById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseCategoryService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
