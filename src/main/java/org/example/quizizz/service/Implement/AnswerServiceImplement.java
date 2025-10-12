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
}
