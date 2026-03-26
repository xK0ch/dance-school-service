package de.tanzschule.service.faq;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public List<FaqResponse> getAll() {
        return faqService.findAll().stream()
                .map(FaqResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public FaqResponse getById(@PathVariable Long id) {
        return FaqResponse.from(faqService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FaqResponse> create(@Valid @RequestBody FaqRequest request) {
        Faq created = faqService.create(request);
        FaqResponse response = FaqResponse.from(created);
        return ResponseEntity.created(URI.create("/api/faqs/" + created.getId())).body(response);
    }

    @PutMapping("/{id}")
    public FaqResponse update(@PathVariable Long id, @Valid @RequestBody FaqRequest request) {
        return FaqResponse.from(faqService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    public List<FaqResponse> reorder(@RequestBody List<Long> orderedIds) {
        return faqService.reorder(orderedIds).stream()
                .map(FaqResponse::from)
                .toList();
    }
}
