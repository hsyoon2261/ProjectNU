package org.example.projectnu.menu.repository

import org.example.projectnu.menu.entity.MenuList
import org.springframework.data.jpa.repository.JpaRepository

interface MenuListRepository : JpaRepository<MenuList, Long> {
    fun existsByNameAndIdNot(name: String, id: Long): Boolean
    fun existsByName(name: String): Boolean
    fun findByName(name: String): MenuList?
}
