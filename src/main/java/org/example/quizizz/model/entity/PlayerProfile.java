package org.example.quizizz.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "player_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "average_score", nullable = false)
    private Double averageScore;

    @ElementCollection
    @CollectionTable(name = "player_preferred_topics", joinColumns = @JoinColumn(name = "player_profile_id"))
    @Column(name = "topic", nullable = false)
    private List<String> preferredTopics;

    @Column(name = "total_play_time", nullable = false)
    private Integer totalPlayTime;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
