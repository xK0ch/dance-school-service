package de.danceschool.service.faq;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, UUID> {

    List<Faq> findAllByOrderByDisplayOrderAsc();
}
