package org.example.quizizz.model.entity;

public interface SoftDeleted {
    Boolean isDeleted();
    void setDeleted(Boolean deleted);
}
