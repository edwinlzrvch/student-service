package com.course.studentservice.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "enrollments",
    indexes = [
        Index(name = "idx_enrollment_student", columnList = "student_id"),
        Index(name = "idx_enrollment_course", columnList = "course_id"),
        Index(name = "idx_enrollment_status", columnList = "status")
    ]
)
data class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    val enrollmentId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    val student: Student,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    val course: Course,

    @Column(name = "enrollment_date")
    val enrollmentDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    val status: EnrollmentStatus = EnrollmentStatus.Active,

    @Column(name = "grade", precision = 3, scale = 1)
    val grade: BigDecimal? = null,

    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)

enum class EnrollmentStatus {
    Active,
    Dropped,
    Completed
}
