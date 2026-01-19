package com.course.studentservice.service

import com.course.studentservice.dto.*
import com.course.studentservice.entity.Course
import com.course.studentservice.entity.Lecturer
import com.course.studentservice.repository.CourseRepository
import com.course.studentservice.repository.LecturerRepository
import com.course.studentservice.repository.EnrollmentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class CourseService(
    private val courseRepository: CourseRepository,
    private val lecturerRepository: LecturerRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    
    @Transactional
    fun createCourse(request: CreateCourseRequest): CourseDto {
        // Check if course code already exists
        if (courseRepository.existsByCourseCode(request.courseCode)) {
            throw IllegalArgumentException("Course with code ${request.courseCode} already exists")
        }
        
        // Validate lecturer if provided
        val lecturer = if (request.lecturerId != null) {
            lecturerRepository.findById(request.lecturerId)
                .orElseThrow { IllegalArgumentException("Lecturer with id ${request.lecturerId} not found") }
        } else null
        
        // Validate dates
        if (request.startDate != null && request.endDate != null) {
            if (request.startDate.isAfter(request.endDate)) {
                throw IllegalArgumentException("Start date cannot be after end date")
            }
        }
        
        // Use native query to handle JSONB properly
        val courseMetadata = request.courseMetadata?.let { 
            if (it.startsWith("{") || it.startsWith("[")) it else "{\"note\":\"$it\"}"
        }
        
        courseRepository.insertCourseWithJsonb(
            courseCode = request.courseCode,
            title = request.title,
            description = request.description,
            credits = request.credits,
            lecturerId = lecturer?.lecturerId,
            startDate = request.startDate,
            endDate = request.endDate,
            capacity = request.capacity,
            courseMetadata = courseMetadata,
            createdAt = java.time.LocalDateTime.now()
        )
        
        // Find the saved course to return as DTO
        val savedCourse = courseRepository.findByCourseCode(request.courseCode)
            ?: throw IllegalStateException("Failed to create course")
        return mapToDto(savedCourse)
    }
    
    fun getCourseById(id: Long): CourseDto {
        val course = courseRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Course with id $id not found") }
        return mapToDto(course)
    }
    
    fun getCourseByCode(courseCode: String): CourseDto {
        val course = courseRepository.findByCourseCode(courseCode)
            ?: throw IllegalArgumentException("Course with code $courseCode not found")
        return mapToDto(course)
    }
    
    fun getAllCourses(pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findAll(pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun searchCoursesByTitle(title: String, pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByTitleContainingIgnoreCase(title, pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun getCoursesByLecturer(lecturerId: Long, pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByLecturerLecturerId(lecturerId, pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun getTotalCourseCount(): Long {
        return courseRepository.count()
    }
    
    fun getPopularCourses(pageable: Pageable): Page<CourseEnrollmentStatsDto> {
        val courses = courseRepository.findAll()
        val courseStats = courses.map { course ->
            val enrollmentCount = enrollmentRepository.findByCourse(course).size.toLong()
            val enrollmentRate = if (course.capacity != null && course.capacity!! > 0) {
                enrollmentCount.toDouble() / course.capacity!!
            } else 0.0
            
            CourseEnrollmentStatsDto(
                courseId = course.courseId,
                courseCode = course.courseCode,
                title = course.title ?: "",
                enrollmentCount = enrollmentCount,
                capacity = course.capacity,
                enrollmentRate = enrollmentRate
            )
        }.sortedByDescending { it.enrollmentCount }
        
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, courseStats.size)
        val pageStats = courseStats.subList(start, end)
        val page = PageImpl(pageStats, pageable, courseStats.size.toLong())
        
        return page
    }
    
    fun getUpcomingCourses(pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByStartDateAfter(LocalDate.now(), pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun getCoursesByDateRange(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByStartDateBetween(startDate, endDate, pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun getCoursesByCredits(credits: Int, pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByCredits(credits, pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun getCoursesByCreditsRange(minCredits: Int, maxCredits: Int, pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByCreditsBetween(minCredits, maxCredits, pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun getCoursesByMinCapacity(capacity: Int, pageable: Pageable): Page<CourseDto> {
        val courses = courseRepository.findByCapacityGreaterThanEqual(capacity, pageable)
        return courses.map { mapToDto(it) }
    }
    
    fun updateCourse(id: Long, request: UpdateCourseRequest): CourseDto {
        val course = courseRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Course with id $id not found") }
        
        // Check if course code is being changed and if it already exists
        if (request.courseCode != null && request.courseCode != course.courseCode) {
            if (courseRepository.existsByCourseCode(request.courseCode)) {
                throw IllegalArgumentException("Course with code ${request.courseCode} already exists")
            }
        }
        
        // Validate lecturer if being changed
        val lecturer = if (request.lecturerId != null) {
            lecturerRepository.findById(request.lecturerId)
                .orElseThrow { IllegalArgumentException("Lecturer with id ${request.lecturerId} not found") }
        } else course.lecturer
        
        // Validate dates
        val startDate = request.startDate ?: course.startDate
        val endDate = request.endDate ?: course.endDate
        
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw IllegalArgumentException("Start date cannot be after end date")
            }
        }
        
        // Prepare courseMetadata for JSONB - ensure it's valid JSON if provided
        val courseMetadata = request.courseMetadata?.let { 
            if (it.startsWith("{") || it.startsWith("[")) it else "{\"note\":\"$it\"}"
        }
        
        // Use native query to handle JSONB properly
        courseRepository.updateCourseWithJsonb(
            courseId = id,
            courseCode = request.courseCode ?: course.courseCode,
            title = request.title ?: course.title,
            description = request.description ?: course.description,
            credits = request.credits ?: course.credits,
            lecturerId = lecturer?.lecturerId,
            startDate = startDate,
            endDate = endDate,
            capacity = request.capacity ?: course.capacity,
            courseMetadata = courseMetadata
        )
        
        // Find the updated course to return as DTO
        val updatedCourse = courseRepository.findById(id)
            .orElseThrow { IllegalStateException("Failed to update course") }
        return mapToDto(updatedCourse)
    }
    
    fun deleteCourse(id: Long) {
        if (!courseRepository.existsById(id)) {
            throw IllegalArgumentException("Course with id $id not found")
        }
        courseRepository.deleteById(id)
    }
    
    private fun mapToDto(course: Course): CourseDto {
        return CourseDto(
            courseId = course.courseId,
            courseCode = course.courseCode,
            title = course.title,
            description = course.description,
            credits = course.credits,
            lecturer = course.lecturer?.let { mapLecturerToDto(it) },
            startDate = course.startDate,
            endDate = course.endDate,
            capacity = course.capacity,
            courseMetadata = course.courseMetadata,
            createdAt = course.createdAt
        )
    }
    
    private fun mapLecturerToDto(lecturer: Lecturer): LecturerDto {
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
