package org.example.projectnu.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "oauth2")
data class OAuth2Properties(
    var google: GoogleProperties = GoogleProperties(),
    var facebook: FacebookProperties = FacebookProperties(),
    var kakao: KakaoProperties = KakaoProperties(),
    var naver: NaverProperties = NaverProperties(),
    var github: GithubProperties = GithubProperties(),
    var apple: AppleProperties = AppleProperties(),
) {
    fun getProperties(provider: OAuth2Provider): Any {
        return when (provider) {
            OAuth2Provider.GOOGLE -> google
            OAuth2Provider.FACEBOOK -> facebook
            OAuth2Provider.KAKAO -> kakao
            OAuth2Provider.NAVER -> naver
            OAuth2Provider.GITHUB -> github
            OAuth2Provider.APPLE -> apple
        }
    }
}


enum class OAuth2Provider {
    GOOGLE,
    FACEBOOK,
    KAKAO,
    NAVER,
    GITHUB,
    APPLE
}

data class GoogleProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)


data class FacebookProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)

data class KakaoProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)

data class NaverProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)

data class GithubProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)

data class AppleProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)
