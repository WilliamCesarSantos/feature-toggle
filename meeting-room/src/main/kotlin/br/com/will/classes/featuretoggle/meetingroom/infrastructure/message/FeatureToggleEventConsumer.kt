package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class FeatureToggleEventConsumer(
    private val featureToggleState: FeatureToggleState
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun toggleUpdatedConsumer(): Consumer<Message<String>> {
        return Consumer { message ->
            try {
                val payload = message.payload
                logger.info("===== [SPRING CLOUD STREAM] Recebido evento customizado de feature toggle =====")
                logger.info("Payload: {}", payload)

                val headers = message.headers
                logger.info("Headers: {}", headers)

                // TODO: Deserializar e processar a mensagem
                // Por enquanto, apenas logando para confirmar que está funcionando

                logger.info("===== Feature toggle customizado processado via Stream =====")
            } catch (e: Exception) {
                logger.error("===== ERRO ao processar evento customizado via Stream =====", e)
            }
        }
    }

}

