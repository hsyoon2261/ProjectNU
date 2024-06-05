package org.example.projectnu.menu.entity

import jakarta.persistence.*

@Entity
@Table(name = "menu_list")
class MenuList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, unique = true)
    val name: String,
    @Column(nullable = false)
    val category: String,
    @Column(nullable = true)
    val description: String? = null,
    @Column(nullable = true)
    val url: String? = null
)
