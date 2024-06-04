package org.example.projectnu.menu.controller

import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.menu.dto.MenuListDto
import org.example.projectnu.menu.dto.MenuListRequestDto
import org.example.projectnu.menu.service.MenuService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/menus")
class MenuController(private val menuService: MenuService) {

    @GetMapping
    fun getAllMenus(): ResponseEntity<Response<List<MenuListDto>>> {
        val menus = menuService.getAllMenus()
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = menus))
    }

    @GetMapping("/{id}")
    fun getMenuById(@PathVariable id: Long): ResponseEntity<Response<MenuListDto>> {
        val menu = menuService.getMenuById(id)
        return if (menu != null) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = menu))
        } else {
            ResponseEntity.status(400).body(
                Response(
                    ResultCode.NOT_FOUND,
                    subcode = ResultCode.NOT_FOUND.description,
                    message = "Menu not found"
                )
            )
        }
    }

    @PostMapping
    fun createMenu(@RequestBody menuDto: MenuListRequestDto): ResponseEntity<Response<MenuListDto>> {
        val createdMenu = menuService.createMenu(menuDto)
        return ResponseEntity.status(201).body(Response(ResultCode.SUCCESS, data = createdMenu))
    }

    @PutMapping
    fun updateMenu(@RequestBody menuDto: MenuListDto): ResponseEntity<Response<MenuListDto>> {
        return try {
            val updatedMenu = menuService.updateMenu(menuDto)
            if (updatedMenu != null) {
                ResponseEntity.ok(Response(ResultCode.SUCCESS, data = updatedMenu))
            } else {
                ResponseEntity.status(400).body(
                    Response(
                        ResultCode.NOT_FOUND,
                        subcode = ResultCode.NOT_FOUND.description,
                        message = "Menu not found"
                    )
                )
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(
                Response(
                    ResultCode.INVALID_REQUEST,
                    subcode = ResultCode.INVALID_REQUEST.description,
                    message = e.message
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMenu(@PathVariable id: Long): ResponseEntity<Response<Void>> {
        menuService.deleteMenu(id)
        return ResponseEntity.status(204).body(Response(ResultCode.SUCCESS))
    }
}
