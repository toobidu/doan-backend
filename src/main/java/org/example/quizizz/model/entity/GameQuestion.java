package org.example.quizizz.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameQuestion implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "game_session_id", nullable = false)
    private Long gameSessionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "time_limit", nullable = false)
    private Duration timeLimit;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
