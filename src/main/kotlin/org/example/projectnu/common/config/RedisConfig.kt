package org.example.projectnu.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.redis.host}") private val redisHost: String,
    @Value("\${spring.redis.port}") private val redisPort: Int
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration()
        config.hostName = redisHost
        config.port = redisPort
        return LettuceConnectionFactory(config)
    }

    @Bean
    fun redisTemplateMap(): Map<Int, RedisTemplate<String, Any>> {
        val templateMap = mutableMapOf<Int, RedisTemplate<String, Any>>()

        for (dbIndex in 0..15) {
            val config = RedisStandaloneConfiguration()
            config.database = dbIndex
            config.hostName = redisHost
            config.port = redisPort
            val factory = LettuceConnectionFactory(config)
            factory.afterPropertiesSet()

            val template = RedisTemplate<String, Any>()
            template.setConnectionFactory(factory)
            template.keySerializer = StringRedisSerializer()
            template.valueSerializer = StringRedisSerializer()
            template.hashKeySerializer = StringRedisSerializer()
            template.hashValueSerializer = GenericJackson2JsonRedisSerializer()
            template.afterPropertiesSet()

            templateMap[dbIndex] = template
        }

        return templateMap
    }
}
