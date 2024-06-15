package org.example.projectnu.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins()
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedOriginPatterns()
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }

//            override fun addInterceptors(registry: InterceptorRegistry) {
//                registry.addInterceptor(CaptureSessionInterceptor()).addPathPatterns("/api/accounts/signIn")
//            }
        }
    }
}
