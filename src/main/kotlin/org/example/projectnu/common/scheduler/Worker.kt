package org.example.projectnu.common.scheduler

import kotlinx.coroutines.*
import org.example.projectnu.common.event.args.TaskEvent
import java.time.LocalDateTime
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

enum class WorkerStatus {
    READY, IN_PROGRESS, FULL, IDLE
}

class Worker(val id: Int) {
    internal val taskQueue: BlockingQueue<TaskEvent<*>> = LinkedBlockingQueue()
    var status: WorkerStatus = WorkerStatus.READY
    var lastFinished: LocalDateTime? = null

    private val executor = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun processTasks() {
        executor.launch {
            while (true) {
                val taskEvent = taskQueue.poll() ?: break
                processTaskEvent(taskEvent)
            }
            status = if (taskQueue.size >= 100) WorkerStatus.FULL else WorkerStatus.READY
            lastFinished = LocalDateTime.now()
        }
    }

    private suspend fun <T> processTaskEvent(taskEvent: TaskEvent<T>) {
        val result = taskEvent.task.execute()
        taskEvent.result.complete(result)
    }

    internal fun addTask(taskEvent: TaskEvent<*>) {
        taskQueue.offer(taskEvent)
        if (taskQueue.size >= 100) {
            status = WorkerStatus.FULL
        } else if (status == WorkerStatus.IDLE) {
            status = WorkerStatus.READY
        }
        processTasks()
    }

    fun markIdle() {
        executor.launch {
            if (status == WorkerStatus.READY && lastFinished != null &&
                lastFinished!!.plusMinutes(1).isBefore(LocalDateTime.now())) {
                status = WorkerStatus.IDLE
            }
        }
    }

    fun stop() {
        println("Worker $id stopped and removed.")
        executor.cancel()
    }

    fun stopIfIdle() {
        if (status == WorkerStatus.IDLE) {
            stop()
        }
    }
}


