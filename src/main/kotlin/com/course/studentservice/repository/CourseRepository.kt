package com.course.studentservice.repository

import com.course.studentservice.entity.Course
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface CourseRepository : JpaRepository<Course, Long> {
    
    fun findByCourseCode(courseCode: String): Course?
    
    fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<Course>
    
    fun findByLecturerLecturerId(lecturerId: Long, pageable: Pageable): Page<Course>
    
    fun findByStartDateAfter(startDate: LocalDate, pageable: Pageable): Page<Course>
    
    fun findByStartDateBetween(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<Course>
    
    fun findByCredits(credits: Int, pageable: Pageable): Page<Course>
    
    fun findByCreditsBetween(minCredits: Int, maxCredits: Int, pageable: Pageable): Page<Course>
    
    fun findByCapacityGreaterThanEqual(capacity: Int, pageable: Pageable): Page<Course>
    
    fun existsByCourseCode(courseCode: String): Boolean
    
    @Modifying
    @Query(value = """
        INSERT INTO courses (course_code, title, description, credits, lecturer_id, start_date, end_date, capacity, course_metadata, created_at)
        VALUES (:courseCode, :title, :description, :credits, :lecturerId, :startDate, :endDate, :capacity, CAST(:courseMetadata AS jsonb), :createdAt)
    """, nativeQuery = true)
    fun insertCourseWithJsonb(
        @Param("courseCode") courseCode: String,
        @Param("title") title: String?,
        @Param("description") description: String?,
        @Param("credits") credits: Int?,
        @Param("lecturerId") lecturerId: Long?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?,
        @Param("capacity") capacity: Int?,
        @Param("courseMetadata") courseMetadata: String?,
        @Param("createdAt") createdAt: java.time.LocalDateTime
    ): Int
    
    @Modifying
    @Query(value = """
        UPDATE courses 
        SET course_code = :courseCode,
            title = :title,
            description = :description,
            credits = :credits,
            lecturer_id = :lecturerId,
            start_date = :startDate,
            end_date = :endDate,
            capacity = :capacity,
            course_metadata = CASE 
                WHEN :courseMetadata IS NULL THEN course_metadata 
                ELSE CAST(:courseMetadata AS jsonb) 
            END
        WHERE course_id = :courseId
    """, nativeQuery = true)
    fun updateCourseWithJsonb(
        @Param("courseId") courseId: Long,
        @Param("courseCode") courseCode: String,
        @Param("title") title: String?,
        @Param("description") description: String?,
        @Param("credits") credits: Int?,
        @Param("lecturerId") lecturerId: Long?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?,
        @Param("capacity") capacity: Int?,
        @Param("courseMetadata") courseMetadata: String?
    ): Int
}
