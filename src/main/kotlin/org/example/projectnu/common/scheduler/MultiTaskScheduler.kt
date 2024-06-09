package org.example.projectnu.common.scheduler

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.projectnu.common.event.args.Task
import org.example.projectnu.common.event.args.TaskEvent
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Component
class MultiTaskScheduler {
    private val _eventFlow = MutableSharedFlow<TaskEvent<*>>(extraBufferCapacity = 2000000)
    internal val eventFlow = _eventFlow.asSharedFlow()

    private val workers: MutableList<Worker> = mutableListOf()
    private var workerIdCounter = 1
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val executorService = Executors.newCachedThreadPool()

    init {
        addWorker()
        startListeningToEvents()
        startCheckingIdleWorkers()
    }

    private fun startListeningToEvents() {
        scope.launch {
            eventFlow.collect { event ->
                assignTask(event)
            }
        }
    }

    private suspend fun assignTask(taskEvent: TaskEvent<*>) {

        for (worker in workers) {
            if (worker.status == WorkerStatus.READY || worker.status == WorkerStatus.IDLE) {
                worker.addTask(taskEvent)
                return
            }
        }

        addWorker().addTask(taskEvent)

    }

    private fun addWorker(): Worker {
        val worker = Worker(workerIdCounter++)
        workers.add(worker)
        return worker
    }


    private fun startCheckingIdleWorkers() {
        scope.launch {
            while (isActive) {
                delay(10000)
                val iterator = workers.iterator()
                while (iterator.hasNext()) {
                    val worker = iterator.next()
                    worker.markIdle()
                    worker.stopIfIdle()
                }
            }
        }
    }

    private suspend fun <T> publishEvent(task: Task<T>): T {
        val result = CompletableDeferred<T>()
        val event = TaskEvent(task, result)
        _eventFlow.emit(event)
        return result.await()
    }

    private suspend fun <T> publishEventBulk(tasks: List<Task<T>>): List<T> {
        val results = tasks.map { task ->
            val result = CompletableDeferred<T>()
            val event = TaskEvent(task, result)
            event to result
        }

        results.forEach { (event, _) ->
            _eventFlow.emit(event)
        }

        return results.map { (_, result) ->
            result.await()
        }
    }

    suspend fun <T> execute(task: suspend () -> T): T {
        return publishEvent(object : Task<T> {
            override val name: String = "Anonymous Task"
            override suspend fun execute(): T = task()
        })
    }

    suspend fun <T> executeBulk(tasks: List<suspend () -> T>): List<T> {
        val taskList = tasks.map { task ->
            object : Task<T> {
                override val name: String = "Anonymous Task"
                override suspend fun execute(): T = task()
            }
        }

        return publishEventBulk(taskList)
    }

    fun <T> executeSync(task: () -> T): Future<T> {
        return executorService.submit(task)
    }


    fun shutdown() {
        scope.cancel()
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
