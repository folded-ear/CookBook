package com.brennaswitzer.cookbook.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppProperties {
    var publicUrl: String? = null
    val auth = Auth()
    val oauth2 = OAuth2()
    val aws = AWSProperties()

    class Auth {
        var tokenSecret: String? = null
        var tokenExpirationMsec: Long = 0
    }

    class OAuth2 {
        var authorizedRedirectUris: List<String> = ArrayList()
    }
}
