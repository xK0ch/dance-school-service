package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CourseRegistrationService {

    private final JavaMailSender mailSender;
    private final CourseRepository courseRepository;
    private final String recipient;
    private final String fromAddress;

    public CourseRegistrationService(
            JavaMailSender mailSender,
            CourseRepository courseRepository,
            @Value("${contact.recipient}") String recipient,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.courseRepository = courseRepository;
        this.recipient = recipient;
        this.fromAddress = fromAddress;
    }

    public void register(Long courseId, CourseRegistrationRequest request) {
        Course course = courseRepository.findWithTariffsById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id " + courseId + " not found"));

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress);
        mail.setTo(recipient);
        mail.setReplyTo(request.email());
        mail.setSubject("Kursanmeldung: " + course.getName() + " - " + request.firstName() + " " + request.lastName());

        StringBuilder body = new StringBuilder();
        body.append("Neue Kursanmeldung\n");
        body.append("==================\n\n");

        body.append("Kurs: ").append(course.getName()).append("\n");
        body.append("Tarif: ").append(request.tariffName()).append("\n\n");

        body.append("Persönliche Daten\n");
        body.append("-----------------\n");
        body.append("Anrede: ").append(request.salutation()).append("\n");
        body.append("Vorname: ").append(request.firstName()).append("\n");
        body.append("Nachname: ").append(request.lastName()).append("\n");
        body.append("Geburtsdatum: ").append(request.birthDate()).append("\n");
        body.append("Straße/Nr.: ").append(request.street()).append("\n");
        body.append("PLZ/Stadt: ").append(request.city()).append("\n");
        body.append("Telefon: ").append(request.phone()).append("\n");
        if (request.mobile() != null && !request.mobile().isBlank()) {
            body.append("Mobil: ").append(request.mobile()).append("\n");
        }
        body.append("E-Mail: ").append(request.email()).append("\n");
        if (request.remark() != null && !request.remark().isBlank()) {
            body.append("Bemerkung: ").append(request.remark()).append("\n");
        }

        body.append("\nPartner\n");
        body.append("-------\n");
        if (Boolean.TRUE.equals(request.withPartner())) {
            body.append("Mit Partner: Ja\n");
            if (request.partnerFirstName() != null && !request.partnerFirstName().isBlank()) {
                body.append("Partner Vorname: ").append(request.partnerFirstName()).append("\n");
            }
            if (request.partnerLastName() != null && !request.partnerLastName().isBlank()) {
                body.append("Partner Nachname: ").append(request.partnerLastName()).append("\n");
            }
        } else {
            body.append("Mit Partner: Nein\n");
        }

        body.append("\nZahlung\n");
        body.append("-------\n");
        if (Boolean.TRUE.equals(request.directDebit())) {
            body.append("Zahlung per Lastschrift: Ja\n");
            if (request.accountHolder() != null && !request.accountHolder().isBlank()) {
                body.append("Kontoinhaber: ").append(request.accountHolder()).append("\n");
            }
            if (request.iban() != null && !request.iban().isBlank()) {
                body.append("IBAN: ").append(request.iban()).append("\n");
            }
            if (request.bic() != null && !request.bic().isBlank()) {
                body.append("BIC: ").append(request.bic()).append("\n");
            }
        } else {
            body.append("Zahlung per Lastschrift: Nein\n");
        }

        mail.setText(body.toString());
        mailSender.send(mail);
    }
}
