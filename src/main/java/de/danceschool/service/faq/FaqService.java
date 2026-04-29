package de.danceschool.service.faq;

import de.danceschool.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public List<Faq> findAll() {
        return faqRepository.findAllByOrderByDisplayOrderAsc();
    }

    public Faq findById(UUID id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ with id " + id + " not found"));
    }

    @Transactional
    public Faq create(FaqRequest request) {
        Faq faq = new Faq(request.question(), request.answer(), request.displayOrder());
        return faqRepository.save(faq);
    }

    @Transactional
    public Faq update(UUID id, FaqRequest request) {
        Faq faq = findById(id);
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setDisplayOrder(request.displayOrder());
        faq.setUpdatedAt(LocalDateTime.now());
        return faqRepository.save(faq);
    }

    @Transactional
    public void delete(UUID id) {
        Faq faq = findById(id);
        faqRepository.delete(faq);
    }

    @Transactional
    public List<Faq> reorder(List<UUID> orderedIds) {
        List<Faq> faqs = faqRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID faqId = orderedIds.get(i);
            Faq faq = faqs.stream()
                    .filter(f -> f.getId().equals(faqId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("FAQ with id " + faqId + " not found"));
            faq.setDisplayOrder(i);
            faq.setUpdatedAt(LocalDateTime.now());
        }
        return faqRepository.saveAll(faqs);
    }
}
