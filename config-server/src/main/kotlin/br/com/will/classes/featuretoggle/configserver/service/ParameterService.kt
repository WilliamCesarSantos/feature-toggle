package br.com.will.classes.featuretoggle.configserver.service

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.PutParameterRequest

@Service
class ParameterService(
    private val ssmClient: SsmClient,
    private val messageService: MessageService
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
        messageService.publishRefreshEvent(
            parameterName,
            parameterValue,
            parameterType
        )
        return "Parameter '$parameterName' updated successfully. Version: ${response.version()}"
    }

}
