package com.course.studentservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "admins")
data class Admin(
    @Id
    @Column(name = "admin_id")
    val adminId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    @MapsId
    val user: User,

    @Column(name = "department", length = 100)
    val department: String? = null
)
