package org.example.projectnu.common.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.example.projectnu.common.annotation.Callee
import org.example.projectnu.common.annotation.Caller
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Aspect
@Component
class CallerAspect(
    private val applicationContext: ApplicationContext // ApplicationContext 주입
) {
    private val methodCache = ConcurrentHashMap<Class<*>, List<Pair<Any, java.lang.reflect.Method>>>()

    @Around("@annotation(caller)")
    fun aroundCaller(joinPoint: ProceedingJoinPoint, caller: Caller): Any? {
        val request = joinPoint.args.firstOrNull()

        if (request == null) {
            return joinPoint.proceed()
        }

        // joinPoint의 대상 클래스에서 @Callee 어노테이션이 있는 메서드를 검색 (캐시 사용)
        val jointPointClass = joinPoint.target::class.java
        val jointPointMethods = methodCache.computeIfAbsent(jointPointClass) {
            it.methods.filter { method ->
                method.isAnnotationPresent(Callee::class.java) &&
                    method.getAnnotation(Callee::class.java).requestType == caller.requestType &&
                    method.getAnnotation(Callee::class.java).responseType == caller.responseType
            }.map { method -> Pair(joinPoint.target, method) }
        }

        // 전체 애플리케이션 컨텍스트에서 @Callee 어노테이션이 있는 메서드를 검색 (캐시 사용)
        val allBeans = applicationContext.getBeansOfType(Any::class.java)
        val calleeMethods = allBeans.values.flatMap { bean ->
            val beanClass = bean::class.java
            methodCache.computeIfAbsent(beanClass) {
                beanClass.methods.filter { method ->
                    method.isAnnotationPresent(Callee::class.java) &&
                        method.getAnnotation(Callee::class.java).requestType == caller.requestType &&
                        method.getAnnotation(Callee::class.java).responseType == caller.responseType
                }.map { method -> Pair(bean, method) }
            }
        }

        // 두 검색 결과를 병합
        val allCalleeMethods = jointPointMethods + calleeMethods

        return if (allCalleeMethods.isNotEmpty()) {
            // Callee 메서드들을 호출하고 결과를 리스트에 담아 반환
            val results = allCalleeMethods.map { (bean, method) ->
                method.invoke(bean, request)
            }
            results
        } else {
            joinPoint.proceed()
        }
    }
}
