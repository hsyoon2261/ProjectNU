package org.example.projectnu.menu.dto

data class MenuListDto(
    var id: Long? = null,
    val name: String,
    val category: String,
    val description: String? = null,
    val url: String? = null
)
