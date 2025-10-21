package com.course.studentservice.service

import com.course.studentservice.dto.*
import com.course.studentservice.entity.Lecturer
import com.course.studentservice.entity.User
import com.course.studentservice.entity.EnrollmentStatus
import com.course.studentservice.repository.LecturerRepository
import com.course.studentservice.repository.CourseRepository
import com.course.studentservice.repository.EnrollmentRepository
import com.course.studentservice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LecturerService(
    private val lecturerRepository: LecturerRepository,
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun createLecturer(request: CreateLecturerRequest): LecturerDto {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with email ${request.email} already exists")
        }
        
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = com.course.studentservice.entity.UserRole.Lecturer
        )
        
        val savedUser = userRepository.save(user)
        
        val lecturer = Lecturer(
            lecturerId = 0,
            user = savedUser,
            specialization = request.specialization,
            phoneNumber = request.phoneNumber,
            hireDate = request.hireDate ?: java.time.LocalDate.now()
        )
        
        val savedLecturer = lecturerRepository.save(lecturer)
        return mapToDto(savedLecturer)
    }
    
    fun getLecturerById(lecturerId: Long): LecturerDto {
        val lecturer = lecturerRepository.findById(lecturerId)
            .orElseThrow { IllegalArgumentException("Lecturer with id $lecturerId not found") }
        return mapToDto(lecturer)
    }
    
    fun getLecturerStats(lecturerId: Long): LecturerStatsDto {
        val lecturer = lecturerRepository.findById(lecturerId)
            .orElseThrow { IllegalArgumentException("Lecturer with id $lecturerId not found") }
        
        val courses = courseRepository.findByLecturerLecturerId(lecturerId, org.springframework.data.domain.Pageable.unpaged())
        val totalCourses = courses.totalElements
        
        val allEnrollments = courses.content.flatMap { course ->
            enrollmentRepository.findByCourse(course)
        }
        
        val totalStudents = allEnrollments.size.toLong()
        val activeStudents = allEnrollments.count { it.status == EnrollmentStatus.Active }.toLong()
        val completedStudents = allEnrollments.count { it.status == EnrollmentStatus.Completed }.toLong()
        
        return LecturerStatsDto(
            lecturerId = lecturerId,
            lecturerName = "${lecturer.user?.firstName ?: ""} ${lecturer.user?.lastName ?: ""}".trim(),
            totalCourses = totalCourses,
            totalStudents = totalStudents,
            activeStudents = activeStudents,
            completedStudents = completedStudents
        )
    }
    
    private fun mapToDto(lecturer: Lecturer): LecturerDto {
        return LecturerDto(
            lecturerId = lecturer.lecturerId,
            user = UserDto(
                userId = lecturer.user.userId,
                firstName = lecturer.user.firstName,
                lastName = lecturer.user.lastName,
                email = lecturer.user.email,
                role = lecturer.user.role
            ),
            specialization = lecturer.specialization,
            hireDate = lecturer.hireDate,
            phoneNumber = lecturer.phoneNumber
        )
    }
}
