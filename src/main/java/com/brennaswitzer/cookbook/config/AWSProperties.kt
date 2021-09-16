package com.brennaswitzer.cookbook.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.aws")
class AWSProperties {
    var region: String? = null
    var accessKey: String? = null
    var secretKey: String? = null
    var bucketName: String? = null
}
