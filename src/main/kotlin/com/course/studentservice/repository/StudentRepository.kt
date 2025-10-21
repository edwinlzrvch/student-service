package com.course.studentservice.repository

import com.course.studentservice.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    
    fun findByUserEmail(email: String): Optional<Student>
    
    fun findByEnrollmentDateBetween(startDate: LocalDate, endDate: LocalDate): List<Student>
    
    fun findByEnrollmentDateAfter(date: LocalDate): List<Student>
    
    fun findByPhoneNumber(phoneNumber: String): Optional<Student>
}
