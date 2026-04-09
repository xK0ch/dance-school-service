package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseCategoryRepository courseCategoryRepository;

    @Mock
    private CourseTariffRepository courseTariffRepository;

    @InjectMocks
    private CourseService courseService;

    private CourseCategory sampleCategory;
    private Course sampleCourse;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sampleCategory = new CourseCategory("Erwachsene", 0);
        sampleCourse = new Course(
                "Welttanzprogramm Teil 1",
                LocalDate.of(2026, 5, 1),
                LocalTime.of(19, 45),
                LocalTime.of(21, 30),
                "8 Doppelstunden",
                "Uwe Höftmann",
                null,
                true,
                sampleCategory
        );
    }

    @Test
    void findById_existingId_returnsCourse() {
        when(courseRepository.findWithTariffsById(id)).thenReturn(Optional.of(sampleCourse));

        CourseResponse result = courseService.findById(id);

        assertThat(result.name()).isEqualTo("Welttanzprogramm Teil 1");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(courseRepository.findWithTariffsById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findById(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingId.toString());
    }

    @Test
    void create_validRequest_savesCourseWithTariffs() {
        CourseRequest request = new CourseRequest(
                "Discofox", LocalDate.of(2026, 6, 1),
                LocalTime.of(20, 0), LocalTime.of(21, 0),
                "4 Stunden", "Tabea Höftmann", "Anfängerkurs", true, categoryId,
                List.of(new CourseTariffRequest("Normal", new BigDecimal("78.00")))
        );

        when(courseCategoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courseRepository.findWithTariffsById(any())).thenReturn(Optional.of(sampleCourse));
        when(courseTariffRepository.save(any(CourseTariff.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseResponse result = courseService.create(request);

        assertThat(result).isNotNull();
        verify(courseRepository).save(any(Course.class));
        verify(courseTariffRepository).save(any(CourseTariff.class));
    }

    @Test
    void create_invalidCategory_throwsException() {
        CourseRequest request = new CourseRequest(
                "Discofox", LocalDate.of(2026, 6, 1),
                LocalTime.of(20, 0), LocalTime.of(21, 0),
                "4 Stunden", "Tabea Höftmann", null, false, nonExistingId, List.of()
        );

        when(courseCategoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingId.toString());
    }

    @Test
    void update_existingId_updatesCourse() {
        when(courseRepository.findWithTariffsById(any())).thenReturn(Optional.of(sampleCourse));
        when(courseCategoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));

        CourseRequest request = new CourseRequest(
                "Updated Course", LocalDate.of(2026, 7, 1),
                LocalTime.of(18, 0), LocalTime.of(19, 30),
                "6 Doppelstunden", "Uwe Höftmann", "Updated remark", false, categoryId,
                List.of()
        );

        CourseResponse result = courseService.update(id, request);

        assertThat(result).isNotNull();
        verify(courseTariffRepository).deleteAllByCourseId(any());
    }

    @Test
    void delete_existingId_deletesCourseAndTariffs() {
        when(courseRepository.findWithTariffsById(id)).thenReturn(Optional.of(sampleCourse));

        courseService.delete(id);

        verify(courseTariffRepository).deleteAllByCourseId(id);
        verify(courseRepository).delete(sampleCourse);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(courseRepository.findWithTariffsById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.delete(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
