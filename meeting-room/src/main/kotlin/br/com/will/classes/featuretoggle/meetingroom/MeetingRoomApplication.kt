package br.com.will.classes.featuretoggle.meetingroom
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class MeetingRoomApplication

fun main(args: Array<String>) {
    runApplication<MeetingRoomApplication>(*args)
}
