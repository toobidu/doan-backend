package org.example.quizizz.service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quizizz.common.exception.ApiException;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.mapper.ExamMapper;
import org.example.quizizz.model.dto.exam.CreateExamRequest;
import org.example.quizizz.model.dto.exam.ExamResponse;
import org.example.quizizz.model.dto.exam.UpdateExamRequest;
import org.example.quizizz.model.entity.Exam;
import org.example.quizizz.model.entity.Topic;
import org.example.quizizz.repository.ExamRepository;
import org.example.quizizz.repository.QuestionRepository;
import org.example.quizizz.repository.TopicRepository;
import org.example.quizizz.repository.AnswerRepository;
import org.example.quizizz.service.Interface.IExamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamServiceImpl implements IExamService {

    private final ExamRepository examRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ExamMapper examMapper;

    @Override
    @Transactional
    public ExamResponse create(CreateExamRequest request, Long teacherId) {
        Topic topic = topicRepository.findById(request.getTopicId())
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Topic not found"));

        Exam exam = examMapper.toEntity(request);
        exam.setTeacherId(teacherId);
        exam = examRepository.save(exam);

        return toResponse(exam, topic.getName());
    }

    @Override
    @Transactional
    public ExamResponse update(Long id, UpdateExamRequest request) {
        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Exam not found"));

        examMapper.updateEntityFromRequest(exam, request);
        exam = examRepository.save(exam);

        Topic topic = topicRepository.findById(exam.getTopicId()).orElse(null);
        return toResponse(exam, topic != null ? topic.getName() : null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Exam not found"));

        questionRepository.findByExamId(id).forEach(question -> {
            answerRepository.findByQuestionId(question.getId()).forEach(answerRepository::delete);
        });
        questionRepository.deleteByExamId(id);
        examRepository.delete(exam);
    }

    @Override
    public ExamResponse getById(Long id) {
        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Exam not found"));

        Topic topic = topicRepository.findById(exam.getTopicId()).orElse(null);
        return toResponse(exam, topic != null ? topic.getName() : null);
    }

    @Override
    public List<ExamResponse> getAll() {
        return examRepository.findAll().stream()
            .map(exam -> {
                Topic topic = topicRepository.findById(exam.getTopicId()).orElse(null);
                return toResponse(exam, topic != null ? topic.getName() : null);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponse> getByTopicId(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new ApiException(404, MessageCode.NOT_FOUND, "Topic not found"));

        return examRepository.findByTopicId(topicId).stream()
            .map(exam -> toResponse(exam, topic.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public Page<ExamResponse> search(String keyword, Long topicId, Pageable pageable) {
        Page<Exam> exams;

        if (topicId != null && keyword != null && !keyword.trim().isEmpty()) {
            exams = examRepository.findByTopicIdAndTitleContainingIgnoreCase(topicId, keyword, pageable);
        } else if (topicId != null) {
            exams = examRepository.findByTopicId(topicId, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            exams = examRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            exams = examRepository.findAll(pageable);
        }

        return exams.map(exam -> {
            Topic topic = topicRepository.findById(exam.getTopicId()).orElse(null);
            return toResponse(exam, topic != null ? topic.getName() : null);
        });
    }

    @Override
    public long count() {
        return examRepository.count();
    }

    @Override
    public List<ExamResponse> getByTeacherId(Long teacherId) {
        return examRepository.findByTeacherId(teacherId).stream()
            .map(exam -> {
                Topic topic = topicRepository.findById(exam.getTopicId()).orElse(null);
                return toResponse(exam, topic != null ? topic.getName() : null);
            })
            .collect(Collectors.toList());
    }

    @Override
    public Page<ExamResponse> searchByTeacher(String keyword, Long topicId, Long teacherId, Pageable pageable) {
        Page<Exam> exams;

        if (teacherId != null && topicId != null && keyword != null && !keyword.trim().isEmpty()) {
            exams = examRepository.findByTeacherIdAndTopicIdAndTitleContainingIgnoreCase(teacherId, topicId, keyword, pageable);
        } else if (teacherId != null && topicId != null) {
            exams = examRepository.findByTeacherIdAndTopicId(teacherId, topicId, pageable);
        } else if (teacherId != null && keyword != null && !keyword.trim().isEmpty()) {
            exams = examRepository.findByTeacherIdAndTitleContainingIgnoreCase(teacherId, keyword, pageable);
        } else if (teacherId != null) {
            exams = examRepository.findByTeacherId(teacherId, pageable);
        } else {
            // Nếu không có teacherId (Admin), lấy tất cả
            return search(keyword, topicId, pageable);
        }

        return exams.map(exam -> {
            Topic topic = topicRepository.findById(exam.getTopicId()).orElse(null);
            return toResponse(exam, topic != null ? topic.getName() : null);
        });
    }

    private ExamResponse toResponse(Exam exam, String topicName) {
        ExamResponse response = examMapper.toResponse(exam);
        response.setTopicName(topicName);
        response.setQuestionCount((int) questionRepository.countByExamId(exam.getId()));
        return response;
    }
}
