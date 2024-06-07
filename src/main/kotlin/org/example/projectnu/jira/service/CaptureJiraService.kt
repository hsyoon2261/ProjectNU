package org.example.projectnu.jira.service

import org.example.projectnu.common.config.JiraProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class CaptureJiraService(
    private val jiraProperties: JiraProperties
) {
    private val restTemplate = RestTemplate()
    @OptIn(ExperimentalEncodingApi::class)
    fun getPageInfo(url : String) : String {
        val auth = jiraProperties.email + ":" + jiraProperties.secret
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        val headers = HttpHeaders()
        headers.set("Authorization", "Basic $encodedAuth")
        headers.set("Accept", "application/json")

        val entity = HttpEntity<String>(headers)
        val response: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java)

        return response.body ?: ""
    }

    fun getSamplePage() : String {
        return getPageInfo("https://ggnetwork.atlassian.net/wiki/api/v2/pages/783983")
    }
}


