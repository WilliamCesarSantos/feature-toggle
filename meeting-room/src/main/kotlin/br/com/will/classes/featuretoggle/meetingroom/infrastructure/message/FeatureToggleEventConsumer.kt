package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import org.slf4j.LoggerFactory
import org.springframework.cloud.bus.event.Destination
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class FeatureToggleEventConsumer(
    private val featureToggleState: FeatureToggleState
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("===== FeatureToggleEventConsumer inicializado =====")
    }

    @Bean
    fun springCloudBus(): Consumer<Message<String>> {
        logger.info("===== Bean springCloudBus sendo criado =====")
        return Consumer { event ->
            try {
                logger.info("Recebido evento de refresh do Spring Cloud Bus: origin={}", "config-server")

                // Atualiza o estado do toggle
                featureToggleState.updateAll(mapOf())

                logger.info("Feature toggles atualizados via Bus")
            } catch (e: Exception) {
                logger.error("Erro ao processar evento de refresh", e)
            }
        }
    }

    @KafkaListener(
        topics = ["toggle-updated-topic"],
        groupId = "meeting-room-toggles",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consumeToggleUpdateEvent(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC, required = false) topic: String?
    ) {
        try {
            logger.info("Recebido evento do tópico toggle-updated-topic: topic={}, message={}", topic, message)

            // TODO: Deserializar e processar a mensagem
            // Por enquanto, apenas logando para confirmar que está funcionando

            logger.info("Evento de toggle processado com sucesso")
        } catch (e: Exception) {
            logger.error("Erro ao processar evento de toggle", e)
        }
    }
}

class FeatureToggleRefreshEvent : RefreshRemoteApplicationEvent {

    var parameterName: String = ""
    var parameterValue: String = ""
    var parameterType: String = ""

    // Construtor padrão necessário para deserialização JSON
    constructor() : super(Any(), "config-server", Destination { "**" })

    constructor(
        source: Any,
        originService: String,
        destinationService: String,
        parameterName: String,
        parameterValue: String,
        parameterType: String
    ) : super(source, originService, Destination { destinationService }) {
        this.parameterName = parameterName
        this.parameterValue = parameterValue
        this.parameterType = parameterType
    }

}
