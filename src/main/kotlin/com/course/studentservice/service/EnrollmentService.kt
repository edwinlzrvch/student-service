package com.course.studentservice.service

import com.course.studentservice.dto.*
import com.course.studentservice.entity.*
import com.course.studentservice.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDate

@Service
@Transactional
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
) {
    
    fun enrollStudent(request: CreateEnrollmentRequest): EnrollmentDto {
        val student = studentRepository.findById(request.studentId)
            .orElseThrow { IllegalArgumentException("Student with id ${request.studentId} not found") }
        
        val course = courseRepository.findById(request.courseId)
            .orElseThrow { IllegalArgumentException("Course with id ${request.courseId} not found") }
        
        // Check if student is already enrolled
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw IllegalArgumentException("Student is already enrolled in this course")
        }
        
        // Check course capacity
        val currentEnrollments = enrollmentRepository.findByCourseAndStatus(course, EnrollmentStatus.Active).size
        if (course.capacity != null && currentEnrollments >= course.capacity!!) {
            throw IllegalArgumentException("Course is at full capacity")
        }
        
        val enrollment = Enrollment(
            student = student,
            course = course,
            enrollmentDate = LocalDateTime.now(),
            status = EnrollmentStatus.Active
        )
        
        val savedEnrollment = enrollmentRepository.save(enrollment)
        return mapToDto(savedEnrollment)
    }
    
    fun dropCourse(enrollmentId: Long): EnrollmentDto {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment with id $enrollmentId not found") }
        
        if (enrollment.status != EnrollmentStatus.Active) {
            throw IllegalArgumentException("Cannot drop a course that is not active")
        }
        
        val updatedEnrollment = enrollment.copy(
            status = EnrollmentStatus.Dropped,
            lastUpdated = LocalDateTime.now()
        )
        
        val savedEnrollment = enrollmentRepository.save(updatedEnrollment)
        return mapToDto(savedEnrollment)
    }
    
    fun updateEnrollment(enrollmentId: Long, request: UpdateEnrollmentRequest): EnrollmentDto {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment with id $enrollmentId not found") }
        
        val updatedEnrollment = enrollment.copy(
            status = request.status ?: enrollment.status,
            grade = request.grade ?: enrollment.grade,
            lastUpdated = LocalDateTime.now()
        )
        
        val savedEnrollment = enrollmentRepository.save(updatedEnrollment)
        return mapToDto(savedEnrollment)
    }
    
    fun getEnrollmentById(enrollmentId: Long): EnrollmentDto {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment with id $enrollmentId not found") }
        return mapToDto(enrollment)
    }
    
    fun getStudentEnrollments(studentId: Long, pageable: Pageable): Page<EnrollmentDto> {
        val student = studentRepository.findById(studentId)
            .orElseThrow { IllegalArgumentException("Student with id $studentId not found") }
        
        val enrollments = enrollmentRepository.findByStudent(student)
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, enrollments.size)
        val pageEnrollments = enrollments.subList(start, end)
        val page = PageImpl(pageEnrollments, pageable, enrollments.size.toLong())
        
        return page.map { mapToDto(it) }
    }
    
    fun getCourseEnrollments(courseId: Long, pageable: Pageable): Page<EnrollmentDto> {
        val course = courseRepository.findById(courseId)
            .orElseThrow { IllegalArgumentException("Course with id $courseId not found") }
        
        val enrollments = enrollmentRepository.findByCourse(course)
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, enrollments.size)
        val pageEnrollments = enrollments.subList(start, end)
        val page = PageImpl(pageEnrollments, pageable, enrollments.size.toLong())
        
        return page.map { mapToDto(it) }
    }
    
    fun getEnrollmentsByStatus(status: EnrollmentStatus, pageable: Pageable): Page<EnrollmentDto> {
        val enrollments = enrollmentRepository.findByStatus(status)
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, enrollments.size)
        val pageEnrollments = enrollments.subList(start, end)
        val page = PageImpl(pageEnrollments, pageable, enrollments.size.toLong())
        
        return page.map { mapToDto(it) }
    }
    
    fun getEnrollmentStats(): EnrollmentStatsDto {
        val totalEnrollments = enrollmentRepository.count()
        val activeEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.Active).size.toLong()
        val completedEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.Completed).size.toLong()
        val droppedEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.Dropped).size.toLong()
        
        return EnrollmentStatsDto(
            totalEnrollments = totalEnrollments,
            activeEnrollments = activeEnrollments,
            completedEnrollments = completedEnrollments,
            droppedEnrollments = droppedEnrollments
        )
    }
    
    fun getLecturerCourseEnrollments(lecturerId: Long, pageable: Pageable): Page<EnrollmentDto> {
        val courses = courseRepository.findByLecturerLecturerId(lecturerId, Pageable.unpaged())
        val allEnrollments = courses.content.flatMap { course ->
            enrollmentRepository.findByCourse(course)
        }
        
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, allEnrollments.size)
        val pageEnrollments = allEnrollments.subList(start, end)
        val page = PageImpl(pageEnrollments, pageable, allEnrollments.size.toLong())
        
        return page.map { mapToDto(it) }
    }
    
    fun getAllEnrollments(pageable: Pageable): Page<EnrollmentDto> {
        return enrollmentRepository.findAll(pageable).map { mapToDto(it) }
    }
    
    fun getRecentEnrollments(days: Int, pageable: Pageable): Page<EnrollmentDto> {
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
        val enrollments = enrollmentRepository.findByEnrollmentDateBetween(cutoffDate, LocalDateTime.now())
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, enrollments.size)
        val pageEnrollments = enrollments.subList(start, end)
        val page = PageImpl(pageEnrollments, pageable, enrollments.size.toLong())
        
        return page.map { mapToDto(it) }
    }
    
    fun getEnrollmentTrends(startDate: LocalDate, endDate: LocalDate): EnrollmentTrendsDto {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        
        val enrollments = enrollmentRepository.findByEnrollmentDateBetween(startDateTime, endDateTime)
        val dailyEnrollments = enrollments.groupBy { it.enrollmentDate.toLocalDate().toString() }
            .mapValues { it.value.size.toLong() }
        
        val totalEnrollments = enrollments.size.toLong()
        val daysBetween = startDate.until(endDate).days + 1
        val averageDailyEnrollments = if (daysBetween > 0) totalEnrollments.toDouble() / daysBetween.toDouble() else 0.0
        
        return EnrollmentTrendsDto(
            dailyEnrollments = dailyEnrollments,
            totalEnrollments = totalEnrollments,
            averageDailyEnrollments = averageDailyEnrollments
        )
    }
    
    private fun mapToDto(enrollment: Enrollment): EnrollmentDto {
        return EnrollmentDto(
            enrollmentId = enrollment.enrollmentId,
            student = enrollment.student?.let { student ->
                StudentDto(
                    studentId = student.studentId,
                    user = student.user?.let { user ->
                        UserDto(
                            userId = user.userId,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            email = user.email,
                            role = user.role
                        )
                    },
                    dateOfBirth = student.dateOfBirth,
                    phoneNumber = student.phoneNumber,
                    address = student.address,
                    enrollmentDate = student.enrollmentDate
                )
            },
            course = enrollment.course?.let { course ->
                CourseDto(
                    courseId = course.courseId,
                    courseCode = course.courseCode,
                    title = course.title,
                    description = course.description,
                    credits = course.credits,
                    lecturer = course.lecturer?.let { lecturer ->
                        LecturerDto(
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
                    },
                    startDate = course.startDate,
                    endDate = course.endDate,
                    capacity = course.capacity,
                    courseMetadata = course.courseMetadata,
                    createdAt = course.createdAt
                )
            },
            enrollmentDate = enrollment.enrollmentDate,
            status = enrollment.status,
            grade = enrollment.grade,
            lastUpdated = enrollment.lastUpdated
        )
    }
}
