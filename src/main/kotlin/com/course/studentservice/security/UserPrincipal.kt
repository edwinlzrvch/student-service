package com.course.studentservice.security

import com.course.studentservice.entity.User
import com.course.studentservice.entity.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    val user: User
) : UserDetails {
    
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        user.role?.let { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_${role.name.uppercase()}"))
        }
        return authorities
    }
    
    override fun getPassword(): String {
        return user.passwordHash ?: ""
    }
    
    override fun getUsername(): String {
        return user.email
    }
    
    override fun isAccountNonExpired(): Boolean {
        return true
    }
    
    override fun isAccountNonLocked(): Boolean {
        return true
    }
    
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }
    
    override fun isEnabled(): Boolean {
        return true
    }
    
    fun getCurrentUser(): User = user
    fun getUserId(): Long = user.userId
    fun getUserRole(): UserRole? = user.role
}
