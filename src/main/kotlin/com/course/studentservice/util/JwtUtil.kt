package com.course.studentservice.util

import com.course.studentservice.entity.UserRole
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.function.Function

@Component
class JwtUtil {
    
    @Value("\${jwt.secret:mySecretKey}")
    private lateinit var secret: String
    
    @Value("\${jwt.expiration:86400000}") // 24 hours
    private var expiration: Long = 86400000
    
    @Value("\${jwt.refresh-expiration:604800000}") // 7 days
    private var refreshExpiration: Long = 604800000
    
    private val key: Key by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }
    
    fun extractUsername(token: String): String? {
        return extractClaim(token, Claims::getSubject)
    }
    
    fun extractExpiration(token: String): Date? {
        return extractClaim(token, Claims::getExpiration)
    }
    
    fun extractRole(token: String): UserRole? {
        val claims = extractAllClaims(token)
        return claims?.get("role", String::class.java)?.let { UserRole.valueOf(it) }
    }
    
    fun extractUserId(token: String): Long? {
        val claims = extractAllClaims(token)
        return claims?.get("userId", Long::class.java)
    }
    
    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T? {
        val claims = extractAllClaims(token)
        return claims?.let { claimsResolver.apply(it) }
    }
    
    private fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }
    
    private fun isTokenExpired(token: String): Boolean {
        val expiration = extractExpiration(token)
        return expiration?.before(Date()) ?: true
    }
    
    fun generateToken(userDetails: UserDetails, userId: Long, role: UserRole): String {
        val claims = HashMap<String, Any>()
        claims["userId"] = userId
        claims["role"] = role.name
        return createToken(claims, userDetails.username, expiration)
    }
    
    fun generateRefreshToken(userDetails: UserDetails, userId: Long, role: UserRole): String {
        val claims = HashMap<String, Any>()
        claims["userId"] = userId
        claims["role"] = role.name
        claims["type"] = "refresh"
        return createToken(claims, userDetails.username, refreshExpiration)
    }
    
    private fun createToken(claims: Map<String, Any>, subject: String, expiration: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            !isTokenExpired(token)
        } catch (e: JwtException) {
            false
        }
    }
    
    fun isRefreshToken(token: String): Boolean {
        val claims = extractAllClaims(token)
        return claims?.get("type", String::class.java) == "refresh"
    }
}
