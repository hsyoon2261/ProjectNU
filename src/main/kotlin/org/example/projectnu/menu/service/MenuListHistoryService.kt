package org.example.projectnu.menu.service

import kotlinx.coroutines.runBlocking
import org.example.projectnu.menu.dto.MenuListDto
import org.example.projectnu.menu.entity.MenuList
import org.example.projectnu.menu.entity.MenuListHistory
import org.example.projectnu.menu.mapper.toDto
import org.example.projectnu.menu.mapper.toEntity
import org.example.projectnu.menu.repository.MenuListHistoryRepository
import org.springframework.stereotype.Service
import java.time.*
import java.time.temporal.ChronoUnit

@Service
class MenuListHistoryService(
    private val menuListHistoryRepository: MenuListHistoryRepository,
    private val menuService: MenuService
) {

    suspend fun getTodayMenuList(): List<MenuListDto> {
        val menuList = menuService.getAllMenus()
        return getRandomMenuItems(menuList, 3)
    }

    suspend fun makeDummyHistory() {
        val menuListDto = menuService.getAllMenus()
        val menuList = menuListDto.map { it.toEntity() }
        val random = java.util.Random()
        val now = LocalDateTime.now()
        val histories = menuList.map { menu ->
            val daysAgo = random.nextInt(5) + 3 // 3~7일 전
            val selectedAt = now.minusDays(daysAgo.toLong())
            MenuListHistory(selectedAt = selectedAt, menuList = menu)
        }
        menuListHistoryRepository.saveAll(histories)
    }

    fun makeDummyHistorySync(){
        runBlocking {
            makeDummyHistory()
        }
    }


    fun getRecentHistories(): List<MenuListHistory> {
        val sevenDaysAgo = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(7).toLocalDate().atStartOfDay().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
        return menuListHistoryRepository.findRecentHistories(sevenDaysAgo)
    }

    fun getRandomMenuItems(menuListDto: List<MenuListDto>, count: Int): List<MenuListDto> {

        val recentHistories = getRecentHistories()
        val weightedList = menuListDto.flatMap { menu -> List(10) { menu } }.toMutableList()
        val recentFilteredHistories  = recentHistories
            .filter { history ->
                val dayOfWeek = history.selectedAt?.dayOfWeek
                dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
            }.sortedByDescending { it.selectedAt }


        val today = LocalDate.now(ZoneId.of("Asia/Seoul"))

        for (history in recentFilteredHistories) {
            // 히스토리의 날짜를 KST 기준 00:00으로 설정
            val historyDate = history.selectedAt?.atZone(ZoneId.of("Asia/Seoul"))?.toLocalDate()

            if (historyDate != null) {
                val daysDifference = calculateBusinessDays(historyDate, today)
                val menu = history.menuList?.toDto()

                val itemsToRemove = when (daysDifference) {
                    0 -> 10
                    1 -> 8
                    2 -> 6
                    3 -> 4
                    4 -> 2
                    else -> 0
                }
                if (itemsToRemove > 0) {
                    repeat(itemsToRemove) {
                        weightedList.remove(menu)
                    }
                }
                if (daysDifference > 5) {
                    break
                }
            }
        }

        weightedList.shuffle()

        val result = mutableSetOf<MenuListDto>()
        for (item in weightedList) {
            if (result.size == count) break
            result.add(item)
        }

        return result.toList()

    }

    fun calculateBusinessDays(startDate: LocalDate, endDate: LocalDate): Int {
        var date = startDate
        var businessDays = 0

        while (date.isBefore(endDate) || date.isEqual(endDate)) {
            if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
                businessDays++
            }
            date = date.plusDays(1)
        }

        return businessDays
    }

}
