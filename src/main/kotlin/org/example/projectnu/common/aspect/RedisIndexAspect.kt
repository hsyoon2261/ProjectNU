package org.example.projectnu.common.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.example.projectnu.common.annotation.RedisIndex
import org.example.projectnu.common.service.RedisService
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@Aspect
@Component
class RedisIndexAspect(private val redisService: RedisService) {

    @Before("execution(* *(..)) && @annotation(redisIndex)")
    fun beforeMethod(joinPoint: JoinPoint, redisIndex: RedisIndex) {
        redisService.setDb(redisIndex.value)
    }

    @Before("execution(* *(..)) && @within(redisIndex)")
    fun beforeClass(joinPoint: JoinPoint, redisIndex: RedisIndex) {
        redisService.setDb(redisIndex.value)
    }

    @Before("execution(* *(..)) && within(@org.example.projectnu.common.annotation.RedisIndex *)")
    fun beforeClass(joinPoint: JoinPoint) {
        val method = joinPoint.signature.declaringType.getDeclaredMethod(joinPoint.signature.name, *joinPoint.args.map { it::class.java }.toTypedArray())
        val methodAnnotation = method.kotlinFunction?.findAnnotation<RedisIndex>()
        val classAnnotation = joinPoint.target::class.findAnnotation<RedisIndex>()

        val dbIndex = methodAnnotation?.value ?: classAnnotation?.value ?: 0
        redisService.setDb(dbIndex)
    }
}
