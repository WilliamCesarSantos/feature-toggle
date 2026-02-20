package br.com.will.classes.featuretoggle.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.cloud.bus.BusProperties
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MessageService(
    private val streamBridge: StreamBridge,
    private val busProperties: BusProperties
) {

    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    private companion object {
        const val SERVICE_NAME_DEFAULT = "config-server"
        const val SOURCE_ORIGIN = "StreamBridge"
        const val REFRESH_ALL_EVENT_BINDING_NAME = "springCloudBus-out-0"
        const val REFRESH_FEATURE_TOGGLE_EVENT_BINDING_NAME = "toggleUpdated-out-0"
    }

    fun publishRefreshEvent(
        destination: String = "*"
    ) {
        val serviceName = busProperties.id ?: SERVICE_NAME_DEFAULT
        val refreshEvent = RefreshRemoteApplicationEvent(
            SOURCE_ORIGIN,
            serviceName
        ) { destination }

        streamBridge.send(REFRESH_ALL_EVENT_BINDING_NAME, refreshEvent)
        logger.info("Published refresh event to springCloudBus: destination={}", destination)
    }

    fun publishRefreshEvent(
        parameterName: String,
        parameterValue: String,
        parameterType: String,
        destination: String = "*"
    ) {
        val originService = busProperties.id ?: SERVICE_NAME_DEFAULT

        val finalDestination = extractApplicationNameFromParameter(parameterName) ?: destination
        val convertedParameterName = convertParameterNameToPropertyFormat(parameterName)

        logger.info("Publishing toggle update: parameter={}, value={}, destination={}",
            convertedParameterName, parameterValue, finalDestination)

        val refreshEvent = FeatureToggleRefreshEvent(
            id = UUID.randomUUID().toString(),
            source = SOURCE_ORIGIN,
            createdAt = LocalDateTime.now(),
            originService = originService,
            destinationService = finalDestination,
            parameterName = convertedParameterName,
            parameterValue = parameterValue,
            parameterType = parameterType
        )

        val sent = streamBridge.send(REFRESH_FEATURE_TOGGLE_EVENT_BINDING_NAME, refreshEvent)

        if (sent) {
            logger.info("Toggle update published successfully to topic: toggle-updated-topic")
        } else {
            logger.error("Failed to publish toggle update to topic: toggle-updated-topic")
        }
    }

    private fun extractApplicationNameFromParameter(parameterName: String): String? {
        val parts = parameterName.split("/")
        return if (parts.size >= 3 && parts[1] == "config") {
            parts[2]
        } else {
            null
        }
    }

    private fun convertParameterNameToPropertyFormat(parameterName: String): String {
        val parts = parameterName.split("/")
        return if (parts.size >= 4 && parts[1] == "config") {
            parts.drop(3).joinToString(".")
        } else {
            parameterName
        }
    }
}

data class FeatureToggleRefreshEvent(
    val id: String,
    val createdAt: LocalDateTime,
    val source: String,
    val originService: String,
    val destinationService: String,
    val parameterName: String,
    val parameterValue: String,
    val parameterType: String
)
