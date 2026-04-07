package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private CourseRepository courseRepository;

    private CourseRegistrationService registrationService;

    private Course sampleCourse;

    @BeforeEach
    void setUp() {
        registrationService = new CourseRegistrationService(
                mailSender, courseRepository, "info@tsfaf.de", "noreply@tsfaf.de");

        CourseCategory category = new CourseCategory("Erwachsene", 0);
        sampleCourse = new Course(
                "Welttanzprogramm Teil 1",
                LocalDate.of(2026, 5, 1),
                LocalTime.of(19, 45),
                LocalTime.of(21, 30),
                "8 Doppelstunden",
                "Uwe Höftmann",
                null,
                true,
                category
        );
    }

    @Test
    void register_validRequest_sendsEmail() {
        when(courseRepository.findWithTariffsById(1L)).thenReturn(Optional.of(sampleCourse));

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Herr", "Max", "Mustermann", "01.01.1990",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", "0171234567", "max@example.com",
                "Anfänger", "Normal", true, "Anna", "Mustermann",
                true, "Max Mustermann", "DE89370400440532013000", "COBADEFFXXX"
        );

        registrationService.register(1L, request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();
        assertThat(mail.getTo()).containsExactly("info@tsfaf.de");
        assertThat(mail.getReplyTo()).isEqualTo("max@example.com");
        assertThat(mail.getSubject()).contains("Welttanzprogramm Teil 1");
        assertThat(mail.getText()).contains("Max");
        assertThat(mail.getText()).contains("Mustermann");
        assertThat(mail.getText()).contains("Mit Partner: Ja");
        assertThat(mail.getText()).contains("Zahlung per Lastschrift: Ja");
        assertThat(mail.getText()).contains("DE89370400440532013000");
    }

    @Test
    void register_withoutPartnerAndDirectDebit_sendsEmail() {
        when(courseRepository.findWithTariffsById(1L)).thenReturn(Optional.of(sampleCourse));

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Frau", "Erika", "Muster", "15.03.1985",
                "Hauptstraße 5", "24537 Neumünster",
                "04321123456", null, "erika@example.com",
                null, "Ermäßigt", false, null, null,
                false, null, null, null
        );

        registrationService.register(1L, request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();
        assertThat(mail.getText()).contains("Mit Partner: Nein");
        assertThat(mail.getText()).contains("Zahlung per Lastschrift: Nein");
        assertThat(mail.getText()).doesNotContain("IBAN");
    }

    @Test
    void register_nonExistingCourse_throwsException() {
        when(courseRepository.findWithTariffsById(99L)).thenReturn(Optional.empty());

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Herr", "Max", "Mustermann", "01.01.1990",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", null, "max@example.com",
                null, "Normal", false, null, null,
                false, null, null, null
        );

        assertThatThrownBy(() -> registrationService.register(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
