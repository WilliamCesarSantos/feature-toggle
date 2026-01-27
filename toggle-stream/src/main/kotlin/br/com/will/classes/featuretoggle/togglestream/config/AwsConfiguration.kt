package br.com.will.classes.featuretoggle.togglestream.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

@Configuration
class AwsConfiguration(
    @field:Value("\${aws.sns.endpoint:http://localhost:4566}")
    private val snsEndpoint: String,
    @field:Value("\${aws.region.static:us-east-1}")
    private val region: String,
    @field:Value("\${aws.credentials.access-key:test}")
    private val accessKey: String,
    @field:Value("\${aws.credentials.secret-key:test}")
    private val secretKey: String
) {

    @Bean
    fun snsClient(): SnsClient {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return if (snsEndpoint.contains("localhost") || snsEndpoint.contains("127.0.0.1")) {
            // LocalStack configuration
            SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(java.net.URI(snsEndpoint))
                .build()
        } else {
            // Real AWS configuration
            SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()
        }
    }
}

