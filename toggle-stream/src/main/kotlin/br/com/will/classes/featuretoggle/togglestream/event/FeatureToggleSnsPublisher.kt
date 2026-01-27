package br.com.will.classes.featuretoggle.togglestream.event

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.PublishResponse
import tools.jackson.databind.ObjectMapper

@Service
class FeatureToggleSnsPublisher(
    private val snsClient: SnsClient,
    private val objectMapper: ObjectMapper,
    @field:Value("\${aws.sns.topic-arn:arn:aws:sns:us-east-1:000000000000:feature-toggle-topic}")
    private val topicArn: String
) {

    fun publishToggle(feature: String, enabled: Boolean) {
        val event = FeatureToggleEvent(feature, enabled)
        val message = objectMapper.writeValueAsString(event)

        val request = PublishRequest.builder()
            .topicArn(topicArn)
            .message(message)
            .messageAttributes(
                mapOf(
                    "feature" to MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(feature)
                        .build(),
                    "enabled" to MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(enabled.toString())
                        .build()
                )
            )
            .build()

        try {
            val result: PublishResponse = snsClient.publish(request)
            println("Published toggle event: $feature=$enabled (MessageId: ${result.messageId()})")
        } catch (e: Exception) {
            println("Error publishing toggle event: ${e.message}")
            throw e
        }
    }
}

data class FeatureToggleEvent(
    val feature: String,
    val enabled: Boolean
)

