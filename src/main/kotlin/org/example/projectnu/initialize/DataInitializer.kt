package org.example.projectnu.initialize

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.projectnu.account.service.AccountService
import org.example.projectnu.initialize.event.InitializeEvent
import org.example.projectnu.menu.dto.MenuListRequestDto
import org.example.projectnu.menu.service.MenuService
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataInitializer(
    private val menuService: MenuService,
    private val accountService: AccountService
) {

    @Order(2)
    @Transactional
    @EventListener(InitializeEvent::class)
    suspend fun run(event: InitializeEvent) {
        var res1 = InitializeDataMigration()
        var res2 = InitializeDataHotfix()
        var res3 = InitializeDummyData()
        if (res1 && res2 && res3) {
            event.initStatus[InitType.DB] = true
        }
    }

    private suspend fun InitializeDataHotfix(): Boolean {
        return true
    }


    private suspend fun InitializeDataMigration(): Boolean {
        return true
    }

    private suspend fun InitializeDummyData(): Boolean {
        val resourcePatternResolver = PathMatchingResourcePatternResolver()
        val resources = resourcePatternResolver.getResources("classpath:data/dummy/*.json")
        var mapper = jacksonObjectMapper()
        resources.forEach { resource ->
            val success = when {
                resource.filename!!.contains("menu_list") -> {
                    val menuList: List<MenuListRequestDto> = mapper.readValue(resource.inputStream)
                    menuService.updateMany(menuList)
                }
                else -> true
            }

            if (success == false) {
                throw RuntimeException("Initialization failed for resource: ${resource.filename}")
            }
        }
        return true
    }
}



enum class DataInitType{
    Migration,
    Development,
    Hotfix,
}

enum class DbType {
    MENU_LIST;
    companion object {
        val ALL = entries.toSet()
    }
}
