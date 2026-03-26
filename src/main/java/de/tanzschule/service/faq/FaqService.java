package de.tanzschule.service.faq;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> findAll() {
        return faqRepository.findAllByOrderByDisplayOrderAsc();
    }

    public Faq findById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ with id " + id + " not found"));
    }

    @Transactional
    public Faq create(FaqRequest request) {
        Faq faq = new Faq(request.question(), request.answer(), request.displayOrder());
        return faqRepository.save(faq);
    }

    @Transactional
    public Faq update(Long id, FaqRequest request) {
        Faq faq = findById(id);
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setDisplayOrder(request.displayOrder());
        faq.setUpdatedAt(LocalDateTime.now());
        return faqRepository.save(faq);
    }

    @Transactional
    public void delete(Long id) {
        Faq faq = findById(id);
        faqRepository.delete(faq);
    }

    @Transactional
    public List<Faq> reorder(List<Long> orderedIds) {
        List<Faq> faqs = faqRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            Long faqId = orderedIds.get(i);
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
