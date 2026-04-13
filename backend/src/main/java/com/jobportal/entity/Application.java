package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "job_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // ✅ FIX 1: @Builder.Default ensures status is PENDING from the moment
    // the builder creates the object. Hibernate 7 validates nullable=false
    // constraints BEFORE @PrePersist fires, so without this, status was
    // null at validation time → DataIntegrityViolationException → 500.
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // ✅ FIX 2: Same issue as status — appliedAt is nullable=false but
    // @Builder sets it to null (ignores field initializers without @Builder.Default).
    // Hibernate 7 sees null before @PrePersist gets a chance to set it → 500.
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    // @PrePersist kept as a safety net, but both fields are now
    // initialized at builder time so this is just defensive code.
    @PrePersist
    protected void onCreate() {
        if (this.appliedAt == null) {
            this.appliedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = ApplicationStatus.PENDING;
        }
    }
}