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
@Table(name = "room_players")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomPlayers implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_host", nullable = false)
    private Boolean isHost = false;

    /**
     * Thứ tự tham gia phòng (dùng để xác định host tiếp theo khi host rời phòng)
     */
    @Column(name = "join_order", nullable = false)
    private Integer joinOrder;

    /**
     * Trạng thái: ACTIVE, KICKED, LEFT
     */
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "time_taken")
    private Integer timeTaken;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
