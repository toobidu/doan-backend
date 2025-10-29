package org.example.quizizz.service.Implement;

import org.example.quizizz.mapper.TopicMapper;
import org.example.quizizz.model.dto.topic.CreateTopicRequest;
import org.example.quizizz.model.dto.topic.TopicResponse;
import org.example.quizizz.model.dto.topic.UpdateTopicRequest;
import org.example.quizizz.model.entity.Topic;
import org.example.quizizz.repository.TopicRepository;
import org.example.quizizz.service.Interface.ITopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicServiceImplement implements ITopicService {
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    /**
     * Tạo mới chủ đề.
     * @param request Thông tin chủ đề cần tạo
     * @return Thông tin chủ đề vừa tạo
     */
    @Override
    public TopicResponse create(CreateTopicRequest request) {
        if (topicRepository.existsByName(request.getName())) {
            throw new RuntimeException("Topic already exists");
        }
        Topic topic = topicMapper.toTopic(request);
        topic = topicRepository.save(topic);
        return topicMapper.toTopicResponse(topic);
    }

    /**
     * Cập nhật thông tin chủ đề.
     * @param id Id chủ đề
     * @param request Thông tin cập nhật
     * @return Thông tin chủ đề sau cập nhật
     */
    @Override
    public TopicResponse update(Long id, UpdateTopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topicMapper.updateTopicFromRequest(topic, request);
        topic = topicRepository.save(topic);
        return topicMapper.toTopicResponse(topic);
    }

    /**
     * Xóa chủ đề.
     * @param id Id chủ đề
     */
    @Override
    public void delete(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topicRepository.delete(topic);
    }

    /**
     * Lấy thông tin chủ đề theo id.
     * @param id Id chủ đề
     * @return Thông tin chủ đề
     */
    @Override
    public TopicResponse getById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        return topicMapper.toTopicResponse(topic);
    }

    /**
     * Lấy danh sách tất cả chủ đề.
     * @return Danh sách chủ đề
     */
    @Override
    public List<TopicResponse> getAll() {
        return topicRepository.findAll().stream()
                .map(topicMapper::toTopicResponse)
                .toList();
    }

    /**
     * Tìm kiếm chủ đề với phân trang.
     * @param keyword Từ khóa tìm kiếm
     * @param pageable Thông tin phân trang
     * @return Danh sách chủ đề phân trang
     */
    @Override
    public org.springframework.data.domain.Page<TopicResponse> search(String keyword, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<Topic> topics;
        if (keyword == null || keyword.trim().isEmpty()) {
            topics = topicRepository.findAll(pageable);
        } else {
            topics = topicRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable);
        }
        return topics.map(topicMapper::toTopicResponse);
    }
}
