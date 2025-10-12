package org.example.quizizz.model.dto.game;

public class QuestionResponse {
    private Long questionId;
    private String questionText;
    private String questionType; // MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    private String[] options; // for multiple choice
    private String imageUrl;
    private Integer timeLimit; // seconds
    private Integer points;
    private Integer currentQuestionNumber;
    private Integer totalQuestions;
    private Long startTime; // timestamp when question was sent

    // Constructors
    public QuestionResponse() {
    }

    public QuestionResponse(Long questionId, String questionText, String questionType,
            String[] options, String imageUrl, Integer timeLimit, Integer points,
            Integer currentQuestionNumber, Integer totalQuestions, Long startTime) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.options = options;
        this.imageUrl = imageUrl;
        this.timeLimit = timeLimit;
        this.points = points;
        this.currentQuestionNumber = currentQuestionNumber;
        this.totalQuestions = totalQuestions;
        this.startTime = startTime;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public void setCurrentQuestionNumber(Integer currentQuestionNumber) {
        this.currentQuestionNumber = currentQuestionNumber;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
