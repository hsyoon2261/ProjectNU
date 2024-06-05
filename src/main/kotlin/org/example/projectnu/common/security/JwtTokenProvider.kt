package org.example.projectnu.common.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.example.projectnu.common.exception.custom.UnAuthorizedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expirationTime: Long
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(userDetails: UserDetails): String {
        val claims = Jwts.claims().apply {
            this["loginId"] = userDetails.username
            this["email"] = (userDetails as CustomUserDetails).email
            this["role"] = userDetails.role
        }
        val now = Date()
        val validity = Date(now.time + expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    private fun parseToken(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            throw UnAuthorizedException("Token expired exception")
        } catch (e: Exception) {
            throw UnAuthorizedException("Invalid token")
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseToken(token)
        val username = claims["loginId"] as String
        val email = claims["email"] as String
        val role = claims["role"] as String
        val authorities = listOf(SimpleGrantedAuthority(role))
        val userDetails = CustomUserDetails(username, "", authorities, email, role)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }
}
