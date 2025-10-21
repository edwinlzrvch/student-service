package com.course.studentservice.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_user_email", columnList = "email", unique = true),
        Index(name = "idx_user_role", columnList = "role")
    ]
)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(name = "first_name", length = 50)
    val firstName: String? = null,

    @Column(name = "last_name", length = 50)
    val lastName: String? = null,

    @Column(name = "email", length = 100, nullable = false, unique = true)
    val email: String,

    @Column(name = "password_hash", length = 255)
    val passwordHash: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    val role: UserRole? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class UserRole {
    Student,
    Lecturer,
    Admin
}
