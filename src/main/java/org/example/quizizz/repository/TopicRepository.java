package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
}
