package com.powerstats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_sources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emailId; // unique IMAP message ID

    @Column(nullable = false)
    private String subject;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(nullable = false)
    private LocalDateTime receivedDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "processing_status")
    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ProcessingStatus {
        PENDING,
        PROCESSING,
        SUCCESS,
        FAILED,
        SKIPPED
    }
}
