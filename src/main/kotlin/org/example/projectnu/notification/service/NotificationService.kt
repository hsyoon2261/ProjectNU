package org.example.projectnu.notification.service

import org.example.projectnu.account.service.AccountService
import org.example.projectnu.common.scheduler.MultiTaskScheduler
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.menu.service.MenuService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val accountService: AccountService,
    private val menuService: MenuService,
    private val slackService: SlackService,
    private val scheduler: MultiTaskScheduler,
) {
    @Scheduled(cron = "0 14 03 * * ?", zone = "Asia/Seoul")
    suspend fun sendMenu() {
        val menuList = menuService.getRandomThreeMenuItems()
        val menuNames = menuList.joinToString(separator = "\n") { it.name }
        val message = "오늘의 메뉴 목록:\n$menuNames"

        accountService.sendSlackMessageToAdmin(message)
    }
}
