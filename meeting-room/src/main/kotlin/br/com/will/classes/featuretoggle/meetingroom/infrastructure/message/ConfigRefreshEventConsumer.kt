package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import org.slf4j.LoggerFactory
import org.springframework.cloud.context.refresh.ContextRefresher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

@Configuration
class ConfigRefreshEventConsumer(
    private val contextRefresher: ContextRefresher
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean("configRefreshConsumer")
    fun configRefreshConsumer(): Consumer<Message<String>> {
        return Consumer { message ->
            try {
                logger.info("🔄 Recebida mensagem de refresh do Config Server via Kafka")
                logger.debug("Payload: {}, Headers: {}", message.payload, message.headers)

                // Refresh do contexto Spring - Isso dispara o EnvironmentChangeEvent automaticamente
                logger.info("Iniciando refresh do contexto...")
                contextRefresher.refresh()

                logger.info("✅ Contexto atualizado (EnvironmentChangeListener processará as mudanças)")
            } catch (e: Exception) {
                logger.error("❌ ERRO ao processar evento de refresh", e)
            }
        }
    }

}

