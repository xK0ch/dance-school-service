package de.danceschool.service.news;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, UUID> {

    List<News> findAllByOrderByDisplayOrderAsc();
}
