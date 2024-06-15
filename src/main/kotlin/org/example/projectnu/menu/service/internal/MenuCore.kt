package org.example.projectnu.menu.service.internal

import org.example.projectnu.common.scheduler.MultiTaskScheduler
import org.example.projectnu.menu.dto.MenuListDto
import org.example.projectnu.menu.mapper.toDto
import org.example.projectnu.menu.repository.MenuListRepository


internal class MenuCore(
    private val repository: MenuListRepository,
    private val scheduler: MultiTaskScheduler
) {
    suspend fun getAllMenusCore(): List<MenuListDto> {
        return scheduler.execute {
            repository.findAll().map { menu ->
                menu.toDto()
            }
        }
    }
}
