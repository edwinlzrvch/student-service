package com.course.studentservice.repository

import com.course.studentservice.entity.Enrollment
import com.course.studentservice.entity.EnrollmentStatus
import com.course.studentservice.entity.Student
import com.course.studentservice.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Repository
interface EnrollmentRepository : JpaRepository<Enrollment, Long> {
    
    fun findByStudent(student: Student): List<Enrollment>
    
    fun findByCourse(course: Course): List<Enrollment>

    fun findByStatus(status: EnrollmentStatus): List<Enrollment>

    fun findByEnrollmentDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Enrollment>
    
    fun findByGradeIsNotNull(): List<Enrollment>
    
    fun findByGrade(grade: BigDecimal): List<Enrollment>
    
    fun existsByStudentAndCourse(student: Student, course: Course): Boolean
    
    fun findByCourseAndStatus(course: Course, status: EnrollmentStatus): List<Enrollment>
}
