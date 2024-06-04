package org.example.projectnu.common.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    private var expirationTime: Long = 0

    fun generateToken(userDetails: UserDetails): String {
        val claims = mapOf(
            "loginId" to userDetails.username,
            "email" to (userDetails as CustomUserDetails).email,
            "role" to (userDetails as CustomUserDetails).role
        )

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims: Claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
            return !claims.expiration.before(Date())
        } catch (e: Exception) {
            return false
        }
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            val claims: Claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
            claims["loginId"] as String
        } catch (e: Exception) {
            null
        }
    }
}
