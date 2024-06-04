package org.example.projectnu.common.service

import org.example.projectnu.common.config.SlackProperties
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SlackService(
    private val slackProperties: SlackProperties
) {
    private val restTemplate = RestTemplate()

    fun sendMessage(channel: String, message: String) {
        val payload = mapOf(
            "text" to message,
            "channel" to channel
        )
        val url = slackProperties.webhook.url
        restTemplate.postForEntity(url, payload, String::class.java)
    }

    fun getSlackUrlString(): String {
        val url = slackProperties.webhook.url
        return url
    }

    fun sendTestMessage(testMessage: String) {
        val url = slackProperties.webhook.url
        val name = "richard.yoon"
        val payload = mapOf(
            "text" to testMessage,
            "channel" to "@$name"
        )
        restTemplate.postForEntity(url, payload, String::class.java)
    }
}
