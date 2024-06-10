package org.example.projectnu.menu.repository

import org.example.projectnu.menu.entity.MenuListHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

interface MenuListHistoryRepository : JpaRepository<MenuListHistory, Long> {

    @Query("SELECT m FROM MenuListHistory m WHERE m.selectedAt >= :fromDate")
    fun findRecentHistories(@Param("fromDate") fromDate: LocalDateTime): List<MenuListHistory>}
