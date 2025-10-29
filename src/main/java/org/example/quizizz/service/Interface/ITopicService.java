package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.topic.CreateTopicRequest;
import org.example.quizizz.model.dto.topic.TopicResponse;
import org.example.quizizz.model.dto.topic.UpdateTopicRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITopicService {
    TopicResponse create(CreateTopicRequest request);
    TopicResponse update(Long id, UpdateTopicRequest request);
    void delete(Long id);
    TopicResponse getById(Long id);
    List<TopicResponse> getAll();
    Page<TopicResponse> search(String keyword, Pageable pageable);
}
