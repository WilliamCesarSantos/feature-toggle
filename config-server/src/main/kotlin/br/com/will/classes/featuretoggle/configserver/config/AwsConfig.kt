package br.com.will.classes.featuretoggle.configserver.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.SsmClientBuilder
import java.net.URI

@Configuration
class AwsConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    @ConditionalOnMissingBean(SsmClient::class)
    fun ssmClient(
        @Value("\${spring.cloud.aws.region.static}")
        region: String?,
        @Value("\${spring.cloud.aws.credentials.access-key}")
        accessKey: String?,
        @Value("\${spring.cloud.aws.credentials.secret-key}")
        secretKey: String?,
        @Value("\${spring.cloud.aws.endpoint}")
        endpoint: String?
    ): SsmClient {
        logger.info("Configuring SSM client: region={}, endpoint={}", region, endpoint)

        val builder = SsmClient.builder()
        configureRegion(region, builder)
        configureCredentialsProvider(accessKey, secretKey, builder)
        configureEndpoint(endpoint, builder)

        logger.info("SSM client configured successfully")
        return builder.build()
    }

    private fun configureRegion(
        region: String?,
        builder: SsmClientBuilder
    ) {
        if (region?.isNotBlank() == true) {
            builder.region(Region.of(region))
            logger.debug("Region configured: {}", region)
        }
    }

    private fun configureCredentialsProvider(
        accessKey: String?,
        secretKey: String?,
        builder: SsmClientBuilder
    ) {
        if (accessKey?.isNotBlank() == true && secretKey?.isNotBlank() == true) {
            val credentials = AwsBasicCredentials.create(accessKey, secretKey)
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials))
            logger.debug("Credentials provider configured")
        }
    }

    private fun configureEndpoint(
        endpoint: String?,
        builder: SsmClientBuilder
    ) {
        if (endpoint?.isNotBlank() == true) {
            builder.endpointOverride(URI.create(endpoint))
            logger.debug("Endpoint override configured: {}", endpoint)
        }
    }

}

