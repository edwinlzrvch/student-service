package com.course.studentservice.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "audit_logs",
    indexes = [
        Index(name = "idx_audit_user", columnList = "user_id"),
        Index(name = "idx_audit_timestamp", columnList = "timestamp")
    ]
)
data class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    val logId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    val user: User,

    @Column(name = "action", length = 100)
    val action: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
