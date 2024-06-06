package org.example.projectnu.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.projectnu.common.filter.ExceptionFilter
import org.example.projectnu.common.filter.JwtTokenFilter
import org.example.projectnu.common.filter.RequestLoggingFilter
import org.example.projectnu.common.security.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val requestLoggingFilter: RequestLoggingFilter,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/accounts/register").permitAll() // 해당 엔드포인트 허용
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/**",
                        "/test/**",
                        "/redis-test/**",
                    ).permitAll()
                    .anyRequest().authenticated()
            }.formLogin { it.disable() }
            .addFilterBefore(JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(requestLoggingFilter, JwtTokenFilter::class.java)
            .addFilterBefore(ExceptionFilter(objectMapper), RequestLoggingFilter::class.java)

            .csrf { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}
