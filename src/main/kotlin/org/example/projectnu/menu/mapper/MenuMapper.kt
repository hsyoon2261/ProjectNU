package org.example.projectnu.menu.mapper

import org.example.projectnu.menu.dto.MenuListDto
import org.example.projectnu.menu.entity.MenuList

fun MenuList.toDto(): MenuListDto {
    return MenuListDto(
        id = this.id,
        name = this.name,
        category = this.category,
        description = this.description,
        url = this.url
    )
}

fun MenuListDto.toEntity(): MenuList {
    return MenuList(
        id = this.id,
        name = this.name,
        category = this.category,
        description = this.description,
        url = this.url
    )
}
