package de.tanzschule.service.contact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = new ContactService(mailSender, "info@tsfaf.de", "noreply@tsfaf.de");
    }

    @Test
    void sendContactMessage_sendsEmailWithCorrectFields() {
        ContactRequest request = new ContactRequest("Max Mustermann", "max@example.com", "0123456789", "Hallo, ich habe eine Frage.");

        contactService.sendContactMessage(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getFrom()).isEqualTo("noreply@tsfaf.de");
        assertThat(sent.getTo()).containsExactly("info@tsfaf.de");
        assertThat(sent.getReplyTo()).isEqualTo("max@example.com");
        assertThat(sent.getSubject()).isEqualTo("Kontaktanfrage von Max Mustermann");
        assertThat(sent.getText()).contains("Max Mustermann");
        assertThat(sent.getText()).contains("max@example.com");
        assertThat(sent.getText()).contains("0123456789");
        assertThat(sent.getText()).contains("Hallo, ich habe eine Frage.");
    }

    @Test
    void sendContactMessage_withoutPhone_omitsPhoneLine() {
        ContactRequest request = new ContactRequest("Anna Test", "anna@example.com", null, "Nachricht ohne Telefon.");

        contactService.sendContactMessage(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getText()).doesNotContain("Telefon:");
        assertThat(sent.getText()).contains("Nachricht ohne Telefon.");
    }

    @Test
    void sendContactMessage_withBlankPhone_omitsPhoneLine() {
        ContactRequest request = new ContactRequest("Anna Test", "anna@example.com", "   ", "Test.");

        contactService.sendContactMessage(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).doesNotContain("Telefon:");
    }
}
