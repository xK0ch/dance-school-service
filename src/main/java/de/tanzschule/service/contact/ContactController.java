package de.tanzschule.service.contact;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@Tag(name = "Contact", description = "Contact form")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @Operation(summary = "Send contact message", description = "Sends a contact form message via email (public)")
    @SecurityRequirements
    public ResponseEntity<Void> sendMessage(@Valid @RequestBody ContactRequest request) {
        contactService.sendContactMessage(request);
        return ResponseEntity.ok().build();
    }
}
