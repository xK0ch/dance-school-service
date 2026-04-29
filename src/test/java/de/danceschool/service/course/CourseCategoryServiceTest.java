package de.danceschool.service.course;

import de.danceschool.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();

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
        when(courseCategoryRepository.findWithCoursesById(id)).thenReturn(Optional.of(sampleCategory));

        CourseCategoryResponse result = courseCategoryService.findById(id);

        assertThat(result.name()).isEqualTo("Erwachsene");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(courseCategoryRepository.findWithCoursesById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseCategoryService.findById(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingId.toString());
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
        when(courseCategoryRepository.findWithCoursesById(id)).thenReturn(Optional.of(sampleCategory));
        when(courseCategoryRepository.save(any(CourseCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseCategoryRequest request = new CourseCategoryRequest("Senioren", 2);
        CourseCategoryResponse result = courseCategoryService.update(id, request);

        assertThat(result.name()).isEqualTo("Senioren");
        assertThat(result.displayOrder()).isEqualTo(2);
    }

    @Test
    void delete_existingId_deletesCategory() {
        when(courseCategoryRepository.findWithCoursesById(id)).thenReturn(Optional.of(sampleCategory));

        courseCategoryService.delete(id);

        verify(courseCategoryRepository).delete(sampleCategory);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(courseCategoryRepository.findWithCoursesById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseCategoryService.delete(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
