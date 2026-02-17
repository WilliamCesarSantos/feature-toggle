package br.com.will.classes.featuretoggle.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.cloud.bus.BusProperties
import org.springframework.cloud.bus.event.Destination
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
            this,
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
        val serviceName = busProperties.id ?: SERVICE_NAME_DEFAULT

        val refreshEvent = FeatureToggleRefreshEvent(
            this,
            serviceName,
            destination,
            parameterName,
            parameterValue,
            parameterType
        )

        streamBridge.send(REFRESH_FEATURE_TOGGLE_EVENT_BINDING_NAME, refreshEvent)

        logger.info("Sent custom event: {}", refreshEvent)
    }
}

class FeatureToggleRefreshEvent(
    source: Any,
    origin: String,
    destination: String = "**"
) : RefreshRemoteApplicationEvent(
    source,
    origin,
    Destination { destination }
) {

    lateinit var parameterName: String
    lateinit var parameterValue: String
    lateinit var parameterType: String

    constructor(
        source: Any,
        originService: String,
        destinationService: String,
        parameterName: String,
        parameterValue: String,
        parameterType: String
    ) : this(source, originService, destinationService) {
        this.parameterName = parameterName
        this.parameterValue = parameterValue
        this.parameterType = parameterType
    }

}
