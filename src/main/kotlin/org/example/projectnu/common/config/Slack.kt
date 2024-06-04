package org.example.projectnu.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "slack")
data class SlackProperties(
    var webhook: Webhook = Webhook()
) {
    data class Webhook(
        var url: String = ""
    )
}