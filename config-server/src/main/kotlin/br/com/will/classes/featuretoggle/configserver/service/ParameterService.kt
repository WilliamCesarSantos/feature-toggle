package br.com.will.classes.featuretoggle.configserver.service

import org.springframework.cloud.bus.BusProperties
import org.springframework.cloud.bus.event.Destination
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.PutParameterRequest

@Service
class ParameterService(
    private val ssmClient: SsmClient,
    private val eventPublisher: ApplicationEventPublisher,
    private val busProperties: BusProperties
) {

    fun updateParameter(
        parameterName: String,
        parameterValue: String,
        parameterType: String = "String"
    ): String {
        val request = PutParameterRequest.builder()
            .name(parameterName)
            .value(parameterValue)
            .type(parameterType)
            .overwrite(true)
            .build()

        val response = ssmClient.putParameter(request)
        publishRefreshEvent()
        return "Parameter '$parameterName' updated successfully. Version: ${response.version()}"
    }

    fun publishRefreshEvent(destination: String = "*") {
        val serviceName = busProperties.id ?: "config-server"
        val refreshEvent = RefreshRemoteApplicationEvent(
            this,
            serviceName,
            Destination { destination }
        )
        eventPublisher.publishEvent(refreshEvent)
    }

}
