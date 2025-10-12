package org.example.quizizz.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseEntity implements SoftDeleted{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "system_flag", nullable = false)
    private String systemFlag;

    @Override
    public void setDeleted(Boolean deleted) {
        this.isDelete = deleted;
    }

    @Override
    public Boolean isDeleted() {
        return this.isDelete;
    }
}
