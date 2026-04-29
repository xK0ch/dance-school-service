package de.danceschool.service.faq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faqs")
@Tag(name = "FAQ", description = "Frequently asked questions management")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    @Operation(operationId = "getAllFaqs", summary = "Get all FAQs", description = "Returns all FAQs sorted by display order")
    @SecurityRequirements
    public List<FaqResponse> getAll() {
        return faqService.findAll().stream()
                .map(FaqResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getFaqById", summary = "Get FAQ by ID", description = "Returns a single FAQ by its ID")
    @SecurityRequirements
    public FaqResponse getById(@PathVariable UUID id) {
        return FaqResponse.from(faqService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createFaq", summary = "Create FAQ", description = "Create a new FAQ entry (requires authentication)")
    public ResponseEntity<FaqResponse> create(@Valid @RequestBody FaqRequest request) {
        Faq created = faqService.create(request);
        FaqResponse response = FaqResponse.from(created);
        return ResponseEntity.created(URI.create("/api/faqs/" + created.getId())).body(response);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateFaq", summary = "Update FAQ", description = "Update an existing FAQ entry (requires authentication)")
    public FaqResponse update(@PathVariable UUID id, @Valid @RequestBody FaqRequest request) {
        return FaqResponse.from(faqService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deleteFaq", summary = "Delete FAQ", description = "Delete a FAQ entry (requires authentication)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @Operation(operationId = "reorderFaqs", summary = "Reorder FAQs", description = "Reorder FAQs by providing a list of IDs in the desired order (requires authentication)")
    public List<FaqResponse> reorder(@RequestBody List<UUID> orderedIds) {
        return faqService.reorder(orderedIds).stream()
                .map(FaqResponse::from)
                .toList();
    }
}
