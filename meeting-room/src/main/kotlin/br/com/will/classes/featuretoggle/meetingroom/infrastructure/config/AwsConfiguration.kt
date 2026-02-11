package br.com.will.classes.featuretoggle.meetingroom.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class AwsConfiguration(
    @field:Value("\${aws.sqs.endpoint:http://localhost:4566}")
    private val sqsEndpoint: String,
    @field:Value("\${aws.region.static:sa-east-1}")
    private val region: String,
    @field:Value("\${aws.credentials.access-key:guest}")
    private val accessKey: String,
    @field:Value("\${aws.credentials.secret-key:guest}")
    private val secretKey: String
) {

    @Bean
    fun sqsClient(): SqsClient {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return if (sqsEndpoint.contains("localhost") || sqsEndpoint.contains("127.0.0.1")) {
            // LocalStack configuration
            SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(java.net.URI(sqsEndpoint))
                .build()
        } else {
            // Real AWS configuration
            SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()
        }
    }
}

