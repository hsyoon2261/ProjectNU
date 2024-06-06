package org.example.projectnu.menu.service

import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.menu.dto.MenuListDto
import org.example.projectnu.menu.dto.MenuListRequestDto
import org.example.projectnu.menu.entity.MenuList
import org.example.projectnu.menu.repository.MenuListRepository
import org.springframework.stereotype.Service

@Service
class MenuService(
    private val repository: MenuListRepository,
    private val slackService: SlackService,
) {
    fun getAllMenus(): List<MenuListDto> {
        return repository.findAll().map { menu ->
            MenuListDto(
                id = menu.id,
                name = menu.name,
                category = menu.category,
                description = menu.description,
                url = menu.url
            )
        }
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

    fun deleteMenu(id: Long) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw IllegalArgumentException("Menu not found")
        }
    }
}