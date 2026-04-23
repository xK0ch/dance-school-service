package de.tanzschule.service.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Registration", description = "Public course registration")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationService registrationService;

    @PostMapping("/{id}/register")
    @Operation(operationId = "registerForCourse", summary = "Register for a course", description = "Submit a course registration form (public)")
    @SecurityRequirements
    public ResponseEntity<Void> register(@PathVariable UUID id, @Valid @RequestBody CourseRegistrationRequest request) {
        registrationService.register(id, request);
        return ResponseEntity.ok().build();
    }
}
