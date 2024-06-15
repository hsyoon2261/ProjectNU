package org.example.projectnu.initialize.event

import org.example.projectnu.initialize.InitType

data class InitializeEvent(
    var target: InitType,
    var initStatus: MutableMap<InitType, Boolean> = InitType.entries.associateWith { false }.toMutableMap()
)
