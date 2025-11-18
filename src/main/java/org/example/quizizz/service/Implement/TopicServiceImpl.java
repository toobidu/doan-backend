package org.example.quizizz.service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quizizz.common.exception.ApiException;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.mapper.TopicMapper;
import org.example.quizizz.model.dto.topic.CreateTopicRequest;
import org.example.quizizz.model.dto.topic.TopicResponse;
import org.example.quizizz.model.dto.topic.UpdateTopicRequest;
import org.example.quizizz.model.entity.Topic;
import org.example.quizizz.repository.TopicRepository;
import org.example.quizizz.repository.ExamRepository;
import org.example.quizizz.repository.QuestionRepository;
import org.example.quizizz.repository.AnswerRepository;
import org.example.quizizz.service.Interface.ITopicService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TopicServiceImpl implements ITopicService {

    private final TopicRepository topicRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final TopicMapper topicMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"topics", "topic"}, allEntries = true)
    public TopicResponse create(CreateTopicRequest request) {
        if (topicRepository.existsByName(request.getName())) {
            throw new ApiException(409, MessageCode.BAD_REQUEST, "Topic name already exists");
        }

        Topic topic = topicMapper.toTopic(request);
        topic = topicRepository.save(topic);

        log.info("Created topic: {}", topic.getName());
        return topicMapper.toTopicResponse(topic);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"topics", "topic"}, allEntries = true)
    public TopicResponse update(Long id, UpdateTopicRequest request) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Topic not found"));

        if (request.getName() != null && !request.getName().equals(topic.getName())) {
            if (topicRepository.existsByName(request.getName())) {
                throw new ApiException(409, MessageCode.BAD_REQUEST, "Topic name already exists");
            }
        }

        topicMapper.updateTopicFromRequest(topic, request);
        topic = topicRepository.save(topic);

        log.info("Updated topic: {}", topic.getName());
        return topicMapper.toTopicResponse(topic);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"topics", "topic"}, allEntries = true)
    public void delete(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Topic not found"));

        examRepository.findByTopicId(id).forEach(exam -> {
            questionRepository.findByExamId(exam.getId()).forEach(question -> {
                answerRepository.findByQuestionId(question.getId()).forEach(answerRepository::delete);
            });
            questionRepository.deleteByExamId(exam.getId());
        });
        examRepository.deleteByTopicId(id);
        topicRepository.delete(topic);

        log.info("Deleted topic and all related data: {}", topic.getName());
    }

    @Override
    @Cacheable(value = "topic", key = "#id")
    public TopicResponse getById(Long id) {
        Topic topic = topicRepository.findById(id)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Topic not found"));

        return topicMapper.toTopicResponse(topic);
    }

    @Override
    @Cacheable(value = "topics")
    public List<TopicResponse> getAll() {
        return topicRepository.findAll().stream()
            .map(topicMapper::toTopicResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Page<TopicResponse> search(String keyword, Pageable pageable) {
        Page<Topic> topics;

        if (keyword != null && !keyword.trim().isEmpty()) {
            topics = topicRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable);
        } else {
            topics = topicRepository.findAll(pageable);
        }

        return topics.map(topicMapper::toTopicResponse);
    }

    @Override
    public Long count() {
        return topicRepository.count();
    }
}
