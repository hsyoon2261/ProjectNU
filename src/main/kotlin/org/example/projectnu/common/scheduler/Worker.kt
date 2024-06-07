package org.example.projectnu.common.scheduler

import org.example.projectnu.common.event.args.Task
import org.example.projectnu.common.event.args.TaskEvent
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.time.LocalDateTime

enum class WorkerStatus {
    READY, IN_PROGRESS, FULL, IDLE
}

class Worker(val id: Int) {
    var name = id
    val taskQueue: BlockingQueue<Task> = LinkedBlockingQueue()
    var status: WorkerStatus = WorkerStatus.READY
    var lastFinished: LocalDateTime? = null

    private var executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    fun processTasks() {
        if(taskQueue.isEmpty())
            return

        executor.submit {
            status = WorkerStatus.IN_PROGRESS
            while (true) {
                val task = taskQueue.poll() ?: break
                task.execute()
            }
            status = if (taskQueue.size >= 100) WorkerStatus.FULL else WorkerStatus.READY
            lastFinished = LocalDateTime.now()
        }
    }

    fun addTask(task: Task) {
        if (executor.isShutdown) {
            restartExecutor()
        }
        executor.submit {
            taskQueue.offer(task)
            if (taskQueue.size >= 100) {
                status = WorkerStatus.FULL
            } else if (status == WorkerStatus.IDLE) {
                status = WorkerStatus.READY
            }
        }
    }

    private fun restartExecutor() {
        executor = Executors.newSingleThreadScheduledExecutor()
        status = WorkerStatus.READY
    }

    fun markIdle() {
        if(status == WorkerStatus.IDLE)
            return

        executor.submit {
            if (status == WorkerStatus.READY && lastFinished != null &&
                lastFinished!!.plusMinutes(1).isBefore(LocalDateTime.now())) {
                status = WorkerStatus.IDLE
            }
        }
    }

    fun stop() {
        println("Worker ${name} stopped and removed.")
        executor.shutdown()
    }

    fun stopIfIdle() {
        if (status == WorkerStatus.IDLE) {
            stop()
        }
    }
}
