package org.example.projectnu.common.scheduler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Component
class CommonScheduler {
    private val jobIdCounter = AtomicLong(0)
    private val tasks = ConcurrentHashMap<Long, Disposable>()

    fun register(schedulerStandard: SchedulerStandard, task: () -> Unit): Long {
        return when (schedulerStandard.type) {
            SchedulingType.PERIODIC -> addSchedule(schedulerStandard.period, schedulerStandard.startTime, task)
            SchedulingType.ONE_TIME -> scheduleOneTimeTask(schedulerStandard.period, task)
        }
    }

    suspend fun register(schedulerStandard: SchedulerStandard, task: suspend () -> Unit): Long {
        return when (schedulerStandard.type) {
            SchedulingType.PERIODIC -> addSchedule(schedulerStandard.period, schedulerStandard.startTime, task)
            SchedulingType.ONE_TIME -> scheduleOneTimeTask(schedulerStandard.period, task)
        }
    }

    fun changeScheduledTask(jobId: Long, schedulerStandard: SchedulerStandard, task: () -> Unit): Long {
        cancelScheduledTask(jobId)
        return register(schedulerStandard, task)
    }

    fun cancelScheduledTask(jobId: Long) {
        tasks[jobId]?.dispose()
        tasks.remove(jobId)
    }

    private fun addSchedule(period: Duration, startTime: LocalDateTime? = null, task: () -> Unit): Long {
        val delay = startTime?.let {
            Duration.between(LocalDateTime.now(), it).coerceAtLeast(Duration.ZERO)
        } ?: Duration.ZERO
        val jobId = jobIdCounter.incrementAndGet()
        val scheduledTask = Mono.delay(delay)
            .thenMany(Flux.interval(period))
            .doOnNext { task() }
            .subscribeOn(Schedulers.parallel())
            .then()

        tasks[jobId] = scheduledTask.subscribe()
        return jobId
    }

    private fun addSchedule(
        period: Duration,
        startTime: LocalDateTime? = null,
        task: suspend () -> Unit
    ): Long {
        val delay = startTime?.let {
            Duration.between(LocalDateTime.now(), it).coerceAtLeast(Duration.ZERO)
        } ?: Duration.ZERO
        val jobId = jobIdCounter.incrementAndGet()
        val scheduledTask = mono(Dispatchers.Default) {
            delay(delay.toMillis())
            while (true) {
                launch { task() }
                delay(period.toMillis())
            }
        }.subscribeOn(Schedulers.parallel()).then()

        tasks[jobId] = scheduledTask.subscribe()
        return jobId
    }

    private fun scheduleOneTimeTask(delay: Duration, task: () -> Unit): Long {
        val jobId = jobIdCounter.incrementAndGet()
        val scheduledTask = Mono.delay(delay)
            .doOnNext { task() }
            .subscribeOn(Schedulers.parallel())
            .then()

        tasks[jobId] = scheduledTask.subscribe()
        return jobId
    }

    private fun scheduleOneTimeTask(delay: Duration, task: suspend () -> Unit): Long {
        val jobId = jobIdCounter.incrementAndGet()
        val scheduledTask = mono(Dispatchers.Default) {
            delay(delay.toMillis())
            task()
        }.subscribeOn(Schedulers.parallel()).then()

        tasks[jobId] = scheduledTask.subscribe()
        return jobId
    }
}

enum class SchedulingType {
    PERIODIC, ONE_TIME
}

class SchedulerStandard(
    val type: SchedulingType,
    val period: Duration,
    val startTime: LocalDateTime? = null
)

enum class TargetStatus {
    ACTIVE, INACTIVE
}

data class SchedulerCommand(
    var targetStatus: TargetStatus = TargetStatus.ACTIVE,
    var schedulerStandard: SchedulerStandard = SchedulerStandard(SchedulingType.PERIODIC, Duration.ofSeconds(10))
)

