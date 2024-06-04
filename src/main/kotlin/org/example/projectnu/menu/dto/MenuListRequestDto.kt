package org.example.projectnu.menu.dto

data class MenuListRequestDto(
    val name: String,
    val category: String,
    val description: String? = null,
    val url: String? = null
)
