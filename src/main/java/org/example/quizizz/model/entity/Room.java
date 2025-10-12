package org.example.quizizz.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "room_mode", nullable = false)
    private String roomMode;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    /**
     * Số lượng câu hỏi trong phòng
     */
    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    /**
     * Thời gian trả lời mỗi câu hỏi (giây)
     */
    @Column(name = "countdown_time", nullable = false)
    private Integer countdownTime;

    /**
     * Đánh dấu phòng đã có lịch sử game (không được xóa)
     */
    @Column(name = "has_game_history", nullable = false)
    private Boolean hasGameHistory = false;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
