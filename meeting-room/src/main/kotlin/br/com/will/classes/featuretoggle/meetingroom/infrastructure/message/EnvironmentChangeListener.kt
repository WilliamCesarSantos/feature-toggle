package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import org.slf4j.LoggerFactory
import org.springframework.cloud.context.environment.EnvironmentChangeEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EnvironmentChangeListener(
    private val environment: Environment,
    private val featureToggleState: FeatureToggleState
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onEnvironmentChange(event: EnvironmentChangeEvent) {
        logger.info("╔════════════════════════════════════════════════════════════════")
        logger.info("║ [ENVIRONMENT CHANGE] Evento recebido após refresh")
        logger.info("╠════════════════════════════════════════════════════════════════")

        val changedKeys = event.keys

        if (changedKeys.isEmpty()) {
            logger.info("║   ℹ️  Nenhuma propriedade foi alterada")
        } else {
            logger.info("║ Total de propriedades alteradas: {}", changedKeys.size)

            // Filtra apenas feature toggles
            val toggleKeys = changedKeys.filter { it.startsWith("feature.toggle") }

            if (toggleKeys.isNotEmpty()) {
                logger.info("║")
                logger.info("║ 🔄 Feature Toggles alterados:")
                toggleKeys.forEach { key ->
                    val newValue = environment.getProperty(key)
                    logger.info("║   ✓ {} = {}", key, newValue)
                }
            }

            // Log outras propriedades alteradas
            val otherKeys = changedKeys - toggleKeys.toSet()
            if (otherKeys.isNotEmpty()) {
                logger.info("║")
                logger.info("║ 📝 Outras propriedades alteradas: {}", otherKeys.size)
                otherKeys.forEach { key ->
                    logger.debug("║   • {}", key)
                }
            }
        }

        // Log do estado atual de todos os toggles
        logger.info("╠════════════════════════════════════════════════════════════════")
        logger.info("║ Estado atual dos Feature Toggles:")
        logAllFeatureToggles()

        logger.info("╚════════════════════════════════════════════════════════════════")
        logger.info("✅ Environment atualizado com sucesso!")
    }

    private fun logAllFeatureToggles() {
        val toggleNames = listOf(
            "time-validation",
            "capacity-validation",
            "overlap-validation"
        )

        toggleNames.forEach { toggleName ->
            val isEnabled = featureToggleState.isEnabled(toggleName)
            val icon = if (isEnabled) "🟢" else "🔴"
            logger.info("║   {} {} = {}", icon, toggleName, isEnabled)
        }
    }

}

