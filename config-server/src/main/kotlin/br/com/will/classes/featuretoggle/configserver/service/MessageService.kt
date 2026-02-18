package br.com.will.classes.featuretoggle.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.cloud.bus.BusProperties
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val streamBridge: StreamBridge,
    private val busProperties: BusProperties
) {

    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    private companion object {
        const val SERVICE_NAME_DEFAULT = "config-server"
        const val REFRESH_ALL_EVENT_BINDING_NAME = "springCloudBus-out-0"
        const val REFRESH_FEATURE_TOGGLE_EVENT_BINDING_NAME = "toggleUpdated-out-0"
    }

    fun publishRefreshEvent(
        destination: String = "*"
    ) {
        val serviceName = busProperties.id ?: SERVICE_NAME_DEFAULT
        val refreshEvent = RefreshRemoteApplicationEvent(
            "StreamBridge",
            serviceName
        ) { destination }

        streamBridge.send(REFRESH_ALL_EVENT_BINDING_NAME, refreshEvent)
        logger.info("Sent event: {}", refreshEvent)
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

        logger.info("Publishing refresh event: parameter={}, destination={}", convertedParameterName, finalDestination)

        val refreshEvent = FeatureToggleRefreshEvent(
            source = "config-server",
            originService = originService,
            parameterName = convertedParameterName,
            parameterValue = parameterValue,
            parameterType = parameterType
        )

        streamBridge.send(REFRESH_FEATURE_TOGGLE_EVENT_BINDING_NAME, refreshEvent)

        logger.debug("Event published successfully: destination={}, parameter={}, value={}",
            finalDestination, convertedParameterName, parameterValue)
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
    val source: String,
    val originService: String,
    val parameterName: String,
    val parameterValue: String,
    val parameterType: String
)
