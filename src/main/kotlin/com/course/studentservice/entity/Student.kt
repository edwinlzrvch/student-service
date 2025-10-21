package com.course.studentservice.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "students",
    indexes = [
        Index(name = "idx_student_enrollment_date", columnList = "enrollment_date")
    ]
)
data class Student(
    @Id
    @Column(name = "student_id")
    val studentId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id")
    @MapsId
    val user: User,

    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate? = null,

    @Column(name = "phone_number", length = 20)
    val phoneNumber: String? = null,

    @Column(name = "address", length = 255)
    val address: String? = null,

    @Column(name = "enrollment_date")
    val enrollmentDate: LocalDate? = null
)
