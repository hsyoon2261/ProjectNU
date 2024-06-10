package org.example.projectnu.menu.service

import kotlinx.coroutines.runBlocking
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.scheduler.MultiTaskScheduler
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.menu.dto.MenuListDto
import org.example.projectnu.menu.dto.MenuListRequestDto
import org.example.projectnu.menu.entity.MenuList
import org.example.projectnu.menu.mapper.toEntity
import org.example.projectnu.menu.repository.MenuListRepository
import org.example.projectnu.menu.service.internal.MenuCore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MenuService(
    private val repository: MenuListRepository,
    private val slackService: SlackService,
    private val scheduler: MultiTaskScheduler,
    private val menuListRepository: MenuListRepository,
) {
    private val core = MenuCore(repository,scheduler)

    suspend fun getAllMenus(): List<MenuListDto> {
        return core.getAllMenusCore()
    }

    fun getMenuById(id: Long): MenuListDto? {
        val menu = repository.findById(id).orElse(null)
        return menu?.let {
            MenuListDto(
                id = it.id,
                name = it.name,
                category = it.category,
                description = it.description,
                url = it.url
            )
        }
    }

    @Transactional
    fun createMenu(menuDto: MenuListRequestDto): MenuListDto {
        if (repository.existsByName(menuDto.name)) {
            throw BadRequestException("Menu with name '${menuDto.name}' already exists")
        }
        val menu = MenuList(
            name = menuDto.name,
            category = menuDto.category,
            description = menuDto.description,
            url = menuDto.url
        )
        val savedMenu = repository.save(menu)
        return MenuListDto(
            id = savedMenu.id,
            name = savedMenu.name,
            category = savedMenu.category,
            description = savedMenu.description,
            url = savedMenu.url
        )
    }

    fun updateMenu(menuDto: MenuListDto): MenuListDto? {
        val menu = repository.findById(menuDto.id ?: return null).orElse(null) ?: return null

        menuDto.id = menu.id
        val updatedMenu = MenuList(
            id = menu.id,
            name = menuDto.name,
            category = menuDto.category.ifEmpty { menu.category },
            description = if (menuDto.description.isNullOrEmpty()) menu.description else menuDto.description,
            url = if (menuDto.url.isNullOrEmpty()) menu.url else menuDto.url
        )
        val savedMenu = repository.save(updatedMenu)
        return MenuListDto(
            id = savedMenu.id,
            name = savedMenu.name,
            category = savedMenu.category,
            description = savedMenu.description,
            url = savedMenu.url
        )
    }

    @Transactional
    suspend fun updateMany(menuDtos : List<MenuListRequestDto>) {

        val menuDtoSet = menuDtos.distinctBy { it.name }

        val updateList = menuDtoSet.map { menu ->
            val res = menu.toEntity()
            val legacyMenu = repository.findByName(menu.name)
            if (legacyMenu != null) res.id = legacyMenu.id
            res
        }
        menuListRepository.saveAll(updateList)
    }

    fun deleteMenu(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("Menu not found")
        }
    }

    suspend fun getRandomThreeMenuItems(): List<MenuListDto> {
        val allMenuList = getAllMenus()
        return getRandomMenuItems(allMenuList, 3);
    }

    private fun getRandomMenuItems(menuList: List<MenuListDto>, count: Int): List<MenuListDto> {
        if (menuList.size <= count) {
            return menuList
        }

        val shuffledList = menuList.toMutableList()
        shuffledList.shuffle()

        return shuffledList.take(count)
    }
}
