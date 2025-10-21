package com.course.studentservice.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "lecturers",
    indexes = [
        Index(name = "idx_lecturer_specialization", columnList = "specialization")
    ]
)
data class Lecturer(
    @Id
    @Column(name = "lecturer_id")
    val lecturerId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", referencedColumnName = "user_id")
    @MapsId
    val user: User,

    @Column(name = "specialization", length = 100)
    val specialization: String? = null,

    @Column(name = "hire_date")
    val hireDate: LocalDate? = null,

    @Column(name = "phone_number", length = 20)
    val phoneNumber: String? = null
)
