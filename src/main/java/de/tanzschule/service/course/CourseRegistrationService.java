package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.util.UUID;
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

    public void register(UUID courseId, CourseRegistrationRequest request) {
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
        appendPersonData(body, request.salutation(), request.firstName(), request.lastName(),
                request.birthDate(), request.street(), request.city(),
                request.phone(), request.mobile(), request.email());
        if (isNotBlank(request.remark())) {
            body.append("Bemerkung: ").append(request.remark()).append("\n");
        }

        body.append("\nPartner\n");
        body.append("-------\n");
        if (Boolean.TRUE.equals(request.withPartner())) {
            body.append("Mit Partner: Ja\n");
            appendPersonData(body, request.partnerSalutation(), request.partnerFirstName(), request.partnerLastName(),
                    request.partnerBirthDate(), request.partnerStreet(), request.partnerCity(),
                    request.partnerPhone(), request.partnerMobile(), request.partnerEmail());
        } else {
            body.append("Mit Partner: Nein\n");
        }

        body.append("\nZahlung\n");
        body.append("-------\n");
        if (Boolean.TRUE.equals(request.directDebit())) {
            body.append("Zahlung per Lastschrift: Ja\n");
            appendBankData(body, "Teilnehmer", request.accountHolder(), request.iban(), request.bic());
            if (Boolean.TRUE.equals(request.withPartner())) {
                if (Boolean.TRUE.equals(request.samePaymentDetails())) {
                    body.append("Partner Bankverbindung: Selbe wie Teilnehmer\n");
                } else {
                    appendBankData(body, "Partner", request.partnerAccountHolder(), request.partnerIban(), request.partnerBic());
                }
            }
        } else {
            body.append("Zahlung per Lastschrift: Nein\n");
        }

        mail.setText(body.toString());
        mailSender.send(mail);
    }

    private void appendPersonData(StringBuilder body, String salutation, String firstName, String lastName,
                                  String birthDate, String street, String city,
                                  String phone, String mobile, String email) {
        if (isNotBlank(salutation)) {
            body.append("Anrede: ").append(salutation).append("\n");
        }
        if (isNotBlank(firstName)) {
            body.append("Vorname: ").append(firstName).append("\n");
        }
        if (isNotBlank(lastName)) {
            body.append("Nachname: ").append(lastName).append("\n");
        }
        if (isNotBlank(birthDate)) {
            body.append("Geburtsdatum: ").append(birthDate).append("\n");
        }
        if (isNotBlank(street)) {
            body.append("Straße/Nr.: ").append(street).append("\n");
        }
        if (isNotBlank(city)) {
            body.append("PLZ/Stadt: ").append(city).append("\n");
        }
        if (isNotBlank(phone)) {
            body.append("Telefon: ").append(phone).append("\n");
        }
        if (isNotBlank(mobile)) {
            body.append("Mobil: ").append(mobile).append("\n");
        }
        if (isNotBlank(email)) {
            body.append("E-Mail: ").append(email).append("\n");
        }
    }

    private void appendBankData(StringBuilder body, String label, String accountHolder, String iban, String bic) {
        if (isNotBlank(accountHolder)) {
            body.append(label).append(" Kontoinhaber: ").append(accountHolder).append("\n");
        }
        if (isNotBlank(iban)) {
            body.append(label).append(" IBAN: ").append(iban).append("\n");
        }
        if (isNotBlank(bic)) {
            body.append(label).append(" BIC: ").append(bic).append("\n");
        }
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
