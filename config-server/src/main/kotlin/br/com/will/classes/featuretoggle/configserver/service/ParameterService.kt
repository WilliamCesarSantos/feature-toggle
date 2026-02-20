package br.com.will.classes.featuretoggle.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.PutParameterRequest

@Service
class ParameterService(
    private val ssmClient: SsmClient,
    private val messageService: MessageService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun updateParameter(
        parameterName: String,
        parameterValue: String,
        parameterType: String = "String"
    ): String {
        logger.debug("Updating SSM parameter: name={}, type={}", parameterName, parameterType)

        val request = PutParameterRequest.builder()
            .name(parameterName)
            .value(parameterValue)
            .type(parameterType)
            .overwrite(true)
            .build()

        val response = ssmClient.putParameter(request)
        logger.info("SSM parameter updated: name={}, version={}", parameterName, response.version())

        messageService.publishRefreshEvent(
            parameterName,
            parameterValue,
            parameterType
        )

        return "Parameter '$parameterName' updated successfully. Version: ${response.version()}"
    }

}
