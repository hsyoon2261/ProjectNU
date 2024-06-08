package org.example.projectnu.initialize

import org.example.projectnu.initialize.event.InitializeEvent
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class Initializer(private val eventHandler: ApplicationEventPublisher) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        Initialize()
    }

    private fun Initialize() {
        val event = InitializeEvent(target = InitType.SERVER)
        eventHandler.publishEvent(event)
    }
}


enum class InitType{
    SERVER,
    CACHE,
    DB,
    NOTIFICATION;
    companion object {
        val ALL = InitType.entries.toSet()
    }
}
