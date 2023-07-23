package com.zlrx.thesis.subjectservice.schedule

import com.zlrx.thesis.subjectservice.config.SUBJECT_CLOSED_EVENT_NAME
import com.zlrx.thesis.subjectservice.repository.SubjectRepository
import mu.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

@Configuration
class SubjectExpiryChecker(
    private val subjectRepository: SubjectRepository,
    private val rabbitTemplate: RabbitTemplate

) {

    private val logger = KotlinLogging.logger {}

    @SchedulerLock(
        name = "subject_expiry",
        lockAtLeastFor = "10m",
        lockAtMostFor = "20m"
    )
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    fun watchSubjectExpiry() {
        val subjects = subjectRepository.findExpiredInLastHour()
        subjects.forEach { subject ->
            logger.info { "Subject ${subject.id} expired." }
            rabbitTemplate.convertAndSend(SUBJECT_CLOSED_EVENT_NAME, subject)
        }
    }
}
