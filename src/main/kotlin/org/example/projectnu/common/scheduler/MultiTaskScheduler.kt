package org.example.projectnu.common.scheduler

import org.example.projectnu.common.event.args.Task
import org.example.projectnu.common.event.args.TaskEvent
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CopyOnWriteArrayList

@Component
class MultiTaskScheduler : ApplicationListener<TaskEvent>{
    private val workers: MutableList<Worker> = CopyOnWriteArrayList()
    private var workerIdCounter = 1

    init {
        addWorker()
    }

    override fun onApplicationEvent(event: TaskEvent) {
        assignTask(event.task)
    }

    @Synchronized
    private fun assignTask(task: Task) {
        for (worker in workers) {
            if (worker.status == WorkerStatus.READY) {
                worker.addTask(task)
                return
            }
        }
        for (worker in workers) {
            if (worker.status == WorkerStatus.IDLE) {
                worker.addTask(task)
                return
            }
        }

        addWorker()
        workers.last().addTask(task)
    }

    private fun addWorker() {
        val worker = Worker(workerIdCounter++)
        workers.add(worker)
        println("Worker ${worker.id} added.")
    }

    @Scheduled(fixedRate = 5000)
    public fun processTasks() {
        for (worker in workers) {
            if (worker.status == WorkerStatus.READY || worker.status == WorkerStatus.FULL) {
                worker.processTasks()
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    public fun checkIdleWorkers() {
        val iterator = workers.iterator()
        while (iterator.hasNext()) {
            val worker = iterator.next()
            worker.markIdle()
            worker.stopIfIdle()
        }
    }

    fun shutdown() {
        for (worker in workers) {
            worker.stop()
        }
    }

    fun getWorkersStatus(): Map<String, Any> {
        val statusCount = workers.groupingBy { it.status }.eachCount()
        val workersInfo = workers.map { "Worker ${it.id}: ${it.status} ${it.lastFinished}" }
        return mapOf(
            "workersInfo" to workersInfo,
            "statusCount" to statusCount
        )
    }

    fun getTotalPendingTasks(): Int {
        return workers.sumOf { it.taskQueue.size }
    }
}
