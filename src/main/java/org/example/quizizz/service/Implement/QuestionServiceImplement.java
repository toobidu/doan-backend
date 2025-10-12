package org.example.quizizz.service.Implement;

import org.example.quizizz.mapper.QuestionMapper;
import org.example.quizizz.model.dto.question.*;
import org.example.quizizz.model.entity.Answer;
import org.example.quizizz.model.entity.Question;
import org.example.quizizz.repository.AnswerRepository;
import org.example.quizizz.repository.QuestionRepository;
import org.example.quizizz.service.Interface.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImplement implements IQuestionService {
    
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionMapper questionMapper;

    @Override
    public List<QuestionWithAnswersResponse> getRandomQuestionsWithAnswers(Long topicId, String questionType, int count) {
        List<Question> questions = getRandomQuestions(topicId, questionType, count);
        return questions.stream().map(this::mapToQuestionWithAnswers).toList();
    }

    @Override
    public List<QuestionWithAnswersResponse> getRandomQuestionsForPlayer(Long topicId, String questionType, int count, Long playerId) {
        List<Question> allQuestions = getAllQuestions(topicId, questionType);
        
        // Shuffle based on playerId for consistent randomization per player
        Random random = new Random(playerId.hashCode());
        Collections.shuffle(allQuestions, random);
        
        List<Question> playerQuestions = allQuestions.stream().limit(count).toList();
        return playerQuestions.stream().map(this::mapToQuestionWithAnswers).toList();
    }

    @Override
    public long countAvailableQuestions(Long topicId, String questionType) {
        if (topicId != null && questionType != null) {
            return questionRepository.countByTopicIdAndQuestionType(topicId, questionType);
        } else if (topicId != null) {
            return questionRepository.countByTopicId(topicId);
        }
        return questionRepository.count();
    }

    private List<Question> getRandomQuestions(Long topicId, String questionType, int count) {
        if (topicId != null && questionType != null) {
            return questionRepository.findRandomQuestionsByTopicAndType(topicId, questionType, count);
        } else if (topicId != null) {
            return questionRepository.findRandomQuestionsByTopic(topicId, count);
        } else if (questionType != null) {
            return questionRepository.findRandomQuestionsByType(questionType, count);
        }
        return questionRepository.findAll().stream().limit(count).toList();
    }

    private List<Question> getAllQuestions(Long topicId, String questionType) {
        if (topicId != null && questionType != null) {
            return questionRepository.findAll().stream()
                .filter(q -> q.getTopicId().equals(topicId) && q.getQuestionType().equals(questionType))
                .toList();
        } else if (topicId != null) {
            return questionRepository.findQuestionByTopicId(topicId);
        }
        return questionRepository.findAll();
    }

    private QuestionWithAnswersResponse mapToQuestionWithAnswers(Question question) {
        List<Answer> answers = answerRepository.findByQuestionId(question.getId());
        return new QuestionWithAnswersResponse(
            question.getId(),
            question.getQuestionText(),
            question.getTopicId(),
            question.getQuestionType(),
            answers
        );
    }

    @Override
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        Question question = questionMapper.toEntity(request);
        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public List<QuestionResponse> createBulkQuestions(CreateBulkQuestionsRequest request) {
        List<Question> questions = request.getQuestions().stream()
                .map(questionMapper::toEntity)
                .collect(Collectors.toList());
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        return savedQuestions.stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse updateQuestion(Long id, UpdateQuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionMapper.updateEntityFromRequest(question, request);
        Question updatedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public void deleteBulkQuestions(DeleteBulkQuestionsRequest request) {
        questionRepository.deleteAllById(request.getQuestionIds());
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return questionMapper.toResponse(question);
    }

    @Override
    public List<QuestionWithAnswersResponse> getQuestionsByTopicId(Long topicId) {
        List<Question> questions = questionRepository.findQuestionByTopicId(topicId);
        return questions.stream()
                .map(this::mapToQuestionWithAnswers)
                .collect(Collectors.toList());
    }
}
