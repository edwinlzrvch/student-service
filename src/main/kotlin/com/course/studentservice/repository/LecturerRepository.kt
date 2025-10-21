package com.course.studentservice.repository

import com.course.studentservice.entity.Lecturer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface LecturerRepository : JpaRepository<Lecturer, Long> {
    
    fun findByUserEmail(email: String): Optional<Lecturer>
    
    fun findBySpecialization(specialization: String): List<Lecturer>
    
    fun findBySpecializationContaining(specialization: String): List<Lecturer>
    
    fun findByHireDateBetween(startDate: LocalDate, endDate: LocalDate): List<Lecturer>
    
    fun findByPhoneNumber(phoneNumber: String): Optional<Lecturer>
}
