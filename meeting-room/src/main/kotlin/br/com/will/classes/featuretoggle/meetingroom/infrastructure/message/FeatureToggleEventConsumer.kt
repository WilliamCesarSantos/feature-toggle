package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import org.slf4j.LoggerFactory
import org.springframework.cloud.bus.event.Destination
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class FeatureToggleEventConsumer(
    private val featureToggleState: FeatureToggleState
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("===== FeatureToggleEventConsumer inicializado =====")
    }

    @EventListener
    fun handleRefreshEvent(event: RefreshRemoteApplicationEvent) {
        try {
            logger.info("===== Recebido evento de refresh do Spring Cloud Bus: origin={}, destination={}",
                event.originService, event.destinationService)

            // Atualiza o estado do toggle
            featureToggleState.updateAll(mapOf())

            logger.info("===== Feature toggles atualizados via Bus =====")
        } catch (e: Exception) {
            logger.error("Erro ao processar evento de refresh", e)
        }
    }

    @EventListener
    fun handleCustomToggleRefreshEvent(event: FeatureToggleRefreshEvent) {
        try {
            logger.info("===== Recebido evento customizado de feature toggle: parameter={}, value={}",
                event.parameterName, event.parameterValue)

            // TODO: Processar o evento customizado

            logger.info("===== Feature toggle customizado processado =====")
        } catch (e: Exception) {
            logger.error("Erro ao processar evento customizado de feature toggle", e)
        }
    }

    @KafkaListener(
        topics = ["toggle-updated-topic"],
        groupId = "meeting-room-toggles",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consumeToggleUpdateEvent(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC, required = false) topic: String?,
        @Header(KafkaHeaders.RECEIVED_PARTITION, required = false) partition: Int?,
        @Header(KafkaHeaders.OFFSET, required = false) offset: Long?,
        @Header(KafkaHeaders.ACKNOWLEDGMENT, required = false) acknowledgment: org.springframework.kafka.support.Acknowledgment?
    ) {
        try {
            logger.info("===== KAFKA CONSUMER ACIONADO =====")
            logger.info("Recebido evento do tópico toggle-updated-topic")
            logger.info("Topic: {}, Partition: {}, Offset: {}", topic, partition, offset)
            logger.info("Message: {}", message)

            // TODO: Deserializar e processar a mensagem
            // Por enquanto, apenas logando para confirmar que está funcionando

            // Acknowledge the message
            acknowledgment?.acknowledge()

            logger.info("===== Evento de toggle processado com sucesso =====")
        } catch (e: Exception) {
            logger.error("===== ERRO ao processar evento de toggle =====", e)
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
