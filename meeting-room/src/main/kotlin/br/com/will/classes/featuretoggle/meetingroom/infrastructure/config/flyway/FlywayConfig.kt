package br.com.will.classes.featuretoggle.meetingroom.infrastructure.config.flyway

import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class FlywayConfig(private val dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun flywayInitialize() {
        try {
            logger.info("Iniciando Flyway...")

            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .load()

            val migrateResult = flyway.migrate()

            logger.info("Flyway completado. Migrações aplicadas: {}", migrateResult.migrationsExecuted)
        } catch (e: Exception) {
            logger.error("Erro ao executar Flyway", e)
            throw RuntimeException("Falha ao executar Flyway", e)
        }
    }
}