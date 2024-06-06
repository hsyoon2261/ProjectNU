package org.example.projectnu.common.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redisTemplateMap: Map<Int, RedisTemplate<String, Any>>,
    @Value("\${spring.redis.default-expire-time}") private val defaultExpire: Duration,
) {
    private var currentRedisTemplate: RedisTemplate<String, Any>? = null

    fun setDb(dbIndex: Int) {
        currentRedisTemplate = redisTemplateMap[dbIndex]
            ?: throw IllegalArgumentException("RedisDb is null dbIndex: $dbIndex")
    }

    fun clearDb() {
        currentRedisTemplate = null
    }

    private fun getRedisTemplate(): RedisTemplate<String, Any> {
        return currentRedisTemplate
            ?: throw IllegalStateException("RedisTemplate is not set. Call setDb() first.")
    }

    // String operations
    fun set(key: String, value: String, duration: Duration? = null) {
        val redisTemplate = getRedisTemplate()
        val ops: ValueOperations<String, Any> = redisTemplate.opsForValue()
        val expireTime = duration ?: defaultExpire
        ops.set(key, value, expireTime)
    }

    fun get(key: String): String? {
        val redisTemplate = getRedisTemplate()
        val ops: ValueOperations<String, Any> = redisTemplate.opsForValue()
        return ops.get(key) as? String
    }

    fun delete(key: String) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.delete(key)
    }

    // Expire key
    fun expireKey(key: String, timeout: Duration = defaultExpire): Boolean {
        val redisTemplate = getRedisTemplate()
        return redisTemplate.expire(key, timeout.seconds, TimeUnit.SECONDS)
    }

    // Hash operations
    fun putHashValue(key: String, hashKey: String, value: Any, timeout: Duration = defaultExpire) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForHash<String, Any>().put(key, hashKey, value)
        redisTemplate.expire(key, timeout.seconds, TimeUnit.SECONDS)
    }

    fun getHashValue(key: String, hashKey: String): Any? {
        val redisTemplate = getRedisTemplate()
        return redisTemplate.opsForHash<String, Any>().get(key, hashKey)
    }

    fun deleteHashValue(key: String, vararg hashKeys: String) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForHash<String, Any>().delete(key, *hashKeys)
    }

    // List operations
    fun pushToList(key: String, value: Any, timeout: Duration = defaultExpire) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForList().rightPush(key, value)
        redisTemplate.expire(key, timeout.seconds, TimeUnit.SECONDS)
    }

    fun popFromList(key: String): Any? {
        val redisTemplate = getRedisTemplate()
        return redisTemplate.opsForList().leftPop(key)
    }

    fun getListSize(key: String): Long? {
        val redisTemplate = getRedisTemplate()
        return redisTemplate.opsForList().size(key)
    }

    // Set operations
    fun addToSet(key: String, vararg values: Any, timeout: Duration = defaultExpire) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForSet().add(key, *values)
        redisTemplate.expire(key, timeout.seconds, TimeUnit.SECONDS)
    }

    fun isMemberOfSet(key: String, value: Any): Boolean? {
        val redisTemplate = getRedisTemplate()
        return redisTemplate.opsForSet().isMember(key, value)
    }

    fun removeFromSet(key: String, vararg values: Any) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForSet().remove(key, *values)
    }

    // ZSet (Sorted Set) operations
    fun addToZSet(key: String, value: Any, score: Double, timeout: Duration = defaultExpire) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForZSet().add(key, value, score)
        redisTemplate.expire(key, timeout.seconds, TimeUnit.SECONDS)
    }

    fun removeFromZSet(key: String, vararg values: Any) {
        val redisTemplate = getRedisTemplate()
        redisTemplate.opsForZSet().remove(key, *values)
    }

    fun getZSetRank(key: String, value: Any): Long? {
        val redisTemplate = getRedisTemplate()
        return redisTemplate.opsForZSet().rank(key, value)
    }

    // Flush DB
    fun flushAll() {
        val redisTemplate = getRedisTemplate()
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushDb()
    }
}
