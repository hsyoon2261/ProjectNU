package org.example.projectnu.menu.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "menu_list_histories")
class MenuListHistory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    var menuList: MenuList? = null,

    @Column(nullable = false)
    var selectedAt: LocalDateTime? = null
)
