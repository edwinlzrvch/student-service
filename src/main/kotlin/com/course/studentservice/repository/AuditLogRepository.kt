package com.course.studentservice.repository

import com.course.studentservice.entity.AuditLog
import com.course.studentservice.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {
    
    fun findByUser(user: User): List<AuditLog>

    fun findByAction(action: String): List<AuditLog>
    
    fun findByActionContaining(action: String): List<AuditLog>
    
    fun findByTimestampBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<AuditLog>
    
    fun findByTimestampAfter(date: LocalDateTime): List<AuditLog>
    
    fun findByTimestampBefore(date: LocalDateTime): List<AuditLog>
}
