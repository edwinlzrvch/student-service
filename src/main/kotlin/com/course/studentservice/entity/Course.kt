package com.course.studentservice.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "courses",
    indexes = [
        Index(name = "idx_course_code", columnList = "course_code", unique = true),
        Index(name = "idx_course_lecturer", columnList = "lecturer_id"),
        Index(name = "idx_course_start_date", columnList = "start_date")
    ]
)
data class Course(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    val courseId: Long = 0,

    @Column(name = "course_code", length = 20, nullable = false, unique = true)
    val courseCode: String,

    @Column(name = "title", length = 100)
    val title: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "credits")
    val credits: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", referencedColumnName = "lecturer_id")
    val lecturer: Lecturer? = null,

    @Column(name = "start_date")
    val startDate: LocalDate? = null,

    @Column(name = "end_date")
    val endDate: LocalDate? = null,

    @Column(name = "capacity")
    val capacity: Int? = null,

    @Column(name = "course_metadata", columnDefinition = "JSONB")
    val courseMetadata: String? = null, // JSON string for PostgreSQL JSONB

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
