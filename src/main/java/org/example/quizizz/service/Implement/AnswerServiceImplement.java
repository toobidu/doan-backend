package org.example.quizizz.service.Implement;

import org.example.quizizz.mapper.AnswerMapper;
import org.example.quizizz.model.dto.answer.*;
import org.example.quizizz.model.entity.Answer;
import org.example.quizizz.repository.AnswerRepository;
import org.example.quizizz.service.Interface.IAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImplement implements IAnswerService {

    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;

    /**
     * Tạo mới câu trả lời
     * @param request
     * @return
     */
    @Override
    public AnswerResponse createAnswer(CreateAnswerRequest request) {
        Answer answer = answerMapper.toEntity(request);
        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    public List<AnswerResponse> createBulkAnswers(CreateBulkAnswersRequest request) {
        List<Answer> answers = request.getAnswers().stream()
                .map(answerMapper::toEntity)
                .collect(Collectors.toList());
        List<Answer> savedAnswers = answerRepository.saveAll(answers);
        return savedAnswers.stream()
                .map(answerMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật câu trả lời
     * @param id
     * @param request
     * @return
     */
    @Override
    public AnswerResponse updateAnswer(Long id, UpdateAnswerRequest request) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        answerMapper.updateEntityFromRequest(answer, request);
        Answer updatedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(updatedAnswer);
    }

    /**
     * Xóa câu trả lời
     * @param id
     */
    @Override
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    /**
     * Lấy câu trả lời theo id
     * @param id
     * @return
     */
    @Override
    public AnswerResponse getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        return answerMapper.toResponse(answer);
    }

    /**
     * Lấy danh sách câu trả lời theo id của câu hỏi
     * @param questionId
     * @return
     */
    @Override
    public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream()
                .map(answerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<AnswerResponse> search(
            String keyword, Long questionId, Boolean isCorrect, org.springframework.data.domain.Pageable pageable) {
        
        org.springframework.data.domain.Page<Answer> answers;
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        
        if (questionId != null && isCorrect != null && hasKeyword) {
            answers = answerRepository.findByQuestionIdAndIsCorrectAndAnswerTextContainingIgnoreCase(
                    questionId, isCorrect, keyword, pageable);
        } else if (questionId != null && isCorrect != null) {
            answers = answerRepository.findByQuestionIdAndIsCorrect(questionId, isCorrect, pageable);
        } else if (questionId != null && hasKeyword) {
            answers = answerRepository.findByQuestionIdAndAnswerTextContainingIgnoreCase(
                    questionId, keyword, pageable);
        } else if (isCorrect != null && hasKeyword) {
            answers = answerRepository.findByIsCorrectAndAnswerTextContainingIgnoreCase(
                    isCorrect, keyword, pageable);
        } else if (hasKeyword) {
            answers = answerRepository.findByAnswerTextContainingIgnoreCase(keyword, pageable);
        } else {
            answers = answerRepository.findAll(pageable);
        }
        
        return answers.map(answerMapper::toResponse);
    }
}
