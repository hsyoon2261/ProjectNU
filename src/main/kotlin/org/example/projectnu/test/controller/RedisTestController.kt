package org.example.projectnu.test.controller

import org.example.projectnu.common.annotation.RedisIndex
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.service.RedisService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration

@RedisIndex(12)
@RestController
@RequestMapping("/redis-test")
class RedisTestController(private val redisService: RedisService) {

    @PostMapping("/set")
    fun setValue(
        @RequestParam key: String,
        @RequestParam value: String,
        @RequestParam(required = false) duration: Long?
    ): ResponseEntity<Response<String>> {
        duration?.let {
            redisService.set(key, value, Duration.ofSeconds(it))
        } ?: redisService.set(key, value)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Value set successfully"))
    }

    @GetMapping("/get")
    fun getValue(@RequestParam key: String): ResponseEntity<Response<Any?>> {
        val value = redisService.get(key)
        return if (value != null) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = value))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response(ResultCode.FAILURE, message = "Key not found"))
        }
    }

    @DeleteMapping("/delete")
    fun deleteValue(@RequestParam key: String): ResponseEntity<Response<String>> {
        redisService.delete(key)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Value deleted successfully"))
    }

    @PostMapping("/expire")
    fun expireKey(@RequestParam key: String, @RequestParam timeout: Long): ResponseEntity<Response<Boolean>> {
        val result = redisService.expireKey(key, Duration.ofSeconds(timeout))
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = result))
    }

    @PostMapping("/hash/put")
    fun putHashValue(
        @RequestParam key: String,
        @RequestParam hashKey: String,
        @RequestParam value: String,
        @RequestParam(required = false) timeout: Long?
    ): ResponseEntity<Response<String>> {
        timeout?.let {
            redisService.putHashValue(key, hashKey, value, Duration.ofSeconds(it))
        } ?: redisService.putHashValue(key, hashKey, value)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Hash value set successfully"))
    }

    @GetMapping("/hash/get")
    fun getHashValue(@RequestParam key: String, @RequestParam hashKey: String): ResponseEntity<Response<Any?>> {
        val value = redisService.getHashValue(key, hashKey)
        return if (value != null) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = value))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response(ResultCode.FAILURE, message = "Hash key not found"))
        }
    }

    @DeleteMapping("/hash/delete")
    fun deleteHashValue(@RequestParam key: String, @RequestParam hashKey: String): ResponseEntity<Response<String>> {
        redisService.deleteHashValue(key, hashKey)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Hash value deleted successfully"))
    }

    @PostMapping("/list/push")
    fun pushToList(
        @RequestParam key: String,
        @RequestParam value: String,
        @RequestParam(required = false) timeout: Long?
    ): ResponseEntity<Response<String>> {
        timeout?.let {
            redisService.pushToList(key, value, Duration.ofSeconds(it))
        } ?: redisService.pushToList(key, value)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Value pushed to list successfully"))
    }

    @GetMapping("/list/pop")
    fun popFromList(@RequestParam key: String): ResponseEntity<Response<Any?>> {
        val value = redisService.popFromList(key)
        return if (value != null) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = value))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response(ResultCode.FAILURE, message = "List is empty"))
        }
    }

    @GetMapping("/list/size")
    fun getListSize(@RequestParam key: String): ResponseEntity<Response<Long?>> {
        val size = redisService.getListSize(key)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = size))
    }

    @PostMapping("/set/add")
    fun addToSet(
        @RequestParam key: String,
        @RequestParam values: List<String>,
        @RequestParam(required = false) timeout: Long?
    ): ResponseEntity<Response<String>> {
        timeout?.let {
            redisService.addToSet(key, *values.toTypedArray(), timeout = Duration.ofSeconds(it))
        } ?: redisService.addToSet(key, *values.toTypedArray())
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Values added to set successfully"))
    }

    @GetMapping("/set/ismember")
    fun isMemberOfSet(@RequestParam key: String, @RequestParam value: String): ResponseEntity<Response<Boolean?>> {
        val isMember = redisService.isMemberOfSet(key, value)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = isMember))
    }

    @DeleteMapping("/set/remove")
    fun removeFromSet(@RequestParam key: String, @RequestParam values: List<String>): ResponseEntity<Response<String>> {
        redisService.removeFromSet(key, *values.toTypedArray())
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Values removed from set successfully"))
    }

    @PostMapping("/zset/add")
    fun addToZSet(
        @RequestParam key: String,
        @RequestParam value: String,
        @RequestParam score: Double,
        @RequestParam(required = false) timeout: Long?
    ): ResponseEntity<Response<String>> {
        timeout?.let {
            redisService.addToZSet(key, value, score, Duration.ofSeconds(it))
        } ?: redisService.addToZSet(key, value, score)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Value added to sorted set successfully"))
    }

    @DeleteMapping("/zset/remove")
    fun removeFromZSet(
        @RequestParam key: String,
        @RequestParam values: List<String>
    ): ResponseEntity<Response<String>> {
        redisService.removeFromZSet(key, *values.toTypedArray())
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Values removed from sorted set successfully"))
    }

    @GetMapping("/zset/rank")
    fun getZSetRank(@RequestParam key: String, @RequestParam value: String): ResponseEntity<Response<Long?>> {
        val rank = redisService.getZSetRank(key, value)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = rank))
    }

    @PostMapping("/flush")
    fun flushAll(): ResponseEntity<Response<String>> {
        redisService.flushAll()
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "All data flushed successfully"))
    }
}
