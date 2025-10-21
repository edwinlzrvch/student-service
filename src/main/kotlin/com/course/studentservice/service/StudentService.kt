package com.course.studentservice.service

import com.course.studentservice.dto.*
import com.course.studentservice.entity.Student
import com.course.studentservice.entity.User
import com.course.studentservice.entity.UserRole
import com.course.studentservice.repository.StudentRepository
import com.course.studentservice.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
@Transactional
class StudentService(
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun createStudent(request: CreateStudentRequest): StudentDto {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with email ${request.email} already exists")
        }
        
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.Student
        )
        
        val savedUser = userRepository.save(user)
        
        val student = Student(
            studentId = 0,  // Will be set by @MapsId from user
            user = savedUser,
            dateOfBirth = request.dateOfBirth,
            phoneNumber = request.phoneNumber,
            address = request.address,
            enrollmentDate = request.enrollmentDate ?: LocalDate.now()
        )
        
        val savedStudent = studentRepository.save(student)
        return mapToDto(savedStudent)
    }
    
    fun getStudentById(id: Long): StudentDto {
        val student = studentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Student with id $id not found") }
        return mapToDto(student)
    }
    
    fun getStudentByEmail(email: String): StudentDto {
        val student = studentRepository.findByUserEmail(email)
            .orElseThrow { IllegalArgumentException("Student with email $email not found") }
        return mapToDto(student)
    }
    
    fun updateStudent(id: Long, request: UpdateStudentRequest): StudentDto {
        val student = studentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Student with id $id not found") }
        
        val user = student.user
        val updatedUser = user.copy(
            firstName = request.firstName ?: user.firstName,
            lastName = request.lastName ?: user.lastName,
            email = request.email ?: user.email,
            passwordHash = request.password?.let { passwordEncoder.encode(it) } ?: user.passwordHash
        )
        
        val savedUser = userRepository.save(updatedUser)
        
        val updatedStudent = student.copy(
            user = savedUser,
            dateOfBirth = request.dateOfBirth ?: student.dateOfBirth,
            phoneNumber = request.phoneNumber ?: student.phoneNumber,
            address = request.address ?: student.address,
            enrollmentDate = request.enrollmentDate ?: student.enrollmentDate
        )
        
        val savedStudent = studentRepository.save(updatedStudent)
        return mapToDto(savedStudent)
    }
    
    fun deleteStudent(id: Long) {
        if (!studentRepository.existsById(id)) {
            throw IllegalArgumentException("Student with id $id not found")
        }
        studentRepository.deleteById(id)
    }
    
    fun getAllStudents(pageable: Pageable): Page<StudentDto> {
        return studentRepository.findAll(pageable).map { mapToDto(it) }
    }
    
    fun getStudentsByEnrollmentDateRange(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<StudentDto> {
        return studentRepository.findByEnrollmentDateBetween(startDate, endDate).let { students ->
            val start = pageable.offset.toInt()
            val end = minOf(start + pageable.pageSize, students.size)
            val pageStudents = students.subList(start, end)
            PageImpl(pageStudents, pageable, students.size.toLong())
        }.map { mapToDto(it) }
    }
    
    fun getTotalStudentCount(): Long {
        return studentRepository.count()
    }
    
    fun getActiveStudents(pageable: Pageable): Page<StudentDto> {
        return studentRepository.findAll(pageable).map { mapToDto(it) }
    }
    
    
    private fun mapToDto(student: Student): StudentDto {
        return StudentDto(
            studentId = student.studentId,
            user = UserDto(
                userId = student.user.userId,
                firstName = student.user.firstName,
                lastName = student.user.lastName,
                email = student.user.email,
                role = student.user.role,
                createdAt = student.user.createdAt,
                updatedAt = student.user.updatedAt
            ),
            dateOfBirth = student.dateOfBirth,
            phoneNumber = student.phoneNumber,
            address = student.address,
            enrollmentDate = student.enrollmentDate
        )
    }
}
