package de.danceschool.service.course;

import de.danceschool.service.exception.ResourceNotFoundException;
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
import java.util.UUID;

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
    private final UUID courseId = UUID.randomUUID();

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
    void register_withPartnerAndSeparateBankDetails_sendsEmail() {
        when(courseRepository.findWithTariffsById(courseId)).thenReturn(Optional.of(sampleCourse));

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Herr", "Max", "Mustermann", "01.01.1990",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", "0171234567", "max@example.com",
                "Anfänger", "Normal", true,
                "Frau", "Anna", "Mustermann", "15.06.1992",
                "Andere Straße 5", "54321 Andersstadt",
                "0987654321", null, "anna@example.com",
                true, "Max Mustermann", "DE89370400440532013000", "COBADEFFXXX",
                false, "Anna Mustermann", "DE27100777770209299700", "DEUTDEDB101"
        );

        registrationService.register(courseId, request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();
        assertThat(mail.getTo()).containsExactly("info@tsfaf.de");
        assertThat(mail.getReplyTo()).isEqualTo("max@example.com");
        assertThat(mail.getSubject()).contains("Welttanzprogramm Teil 1");
        assertThat(mail.getText()).contains("Max");
        assertThat(mail.getText()).contains("Mit Partner: Ja");
        assertThat(mail.getText()).contains("Anna");
        assertThat(mail.getText()).contains("anna@example.com");
        assertThat(mail.getText()).contains("Teilnehmer IBAN: DE89370400440532013000");
        assertThat(mail.getText()).contains("Partner IBAN: DE27100777770209299700");
    }

    @Test
    void register_withPartnerAndSameBankDetails_sendsEmail() {
        when(courseRepository.findWithTariffsById(courseId)).thenReturn(Optional.of(sampleCourse));

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Herr", "Max", "Mustermann", "01.01.1990",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", null, "max@example.com",
                null, "Normal", true,
                "Frau", "Anna", "Mustermann", "15.06.1992",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", null, "anna@example.com",
                true, "Max Mustermann", "DE89370400440532013000", null,
                true, null, null, null
        );

        registrationService.register(courseId, request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();
        assertThat(mail.getText()).contains("Partner Bankverbindung: Selbe wie Teilnehmer");
        assertThat(mail.getText()).doesNotContain("Partner IBAN");
    }

    @Test
    void register_withoutPartnerAndDirectDebit_sendsEmail() {
        when(courseRepository.findWithTariffsById(courseId)).thenReturn(Optional.of(sampleCourse));

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Frau", "Erika", "Muster", "15.03.1985",
                "Hauptstraße 5", "24537 Neumünster",
                "04321123456", null, "erika@example.com",
                null, "Ermäßigt", false,
                null, null, null, null, null, null, null, null, null,
                false, null, null, null,
                null, null, null, null
        );

        registrationService.register(courseId, request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();
        assertThat(mail.getText()).contains("Mit Partner: Nein");
        assertThat(mail.getText()).contains("Zahlung per Lastschrift: Nein");
        assertThat(mail.getText()).doesNotContain("IBAN");
    }

    @Test
    void register_nonExistingCourse_throwsException() {
        UUID missingId = UUID.randomUUID();
        when(courseRepository.findWithTariffsById(missingId)).thenReturn(Optional.empty());

        CourseRegistrationRequest request = new CourseRegistrationRequest(
                "Herr", "Max", "Mustermann", "01.01.1990",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", null, "max@example.com",
                null, "Normal", false,
                null, null, null, null, null, null, null, null, null,
                false, null, null, null,
                null, null, null, null
        );

        assertThatThrownBy(() -> registrationService.register(missingId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
