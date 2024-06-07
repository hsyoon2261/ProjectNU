package org.example.projectnu.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jira")
data class JiraProperties (
    var secret : String = "",
    var email : String = ""
)
