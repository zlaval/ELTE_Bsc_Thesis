package com.zlrx.thesis.videoprocessor.stream

import com.chrylis.codec.base58.Base58UUID
import com.zlrx.thesis.videoprocessor.service.VideoProcessor
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.util.*
import java.util.function.Consumer

enum class VideoState {
    UPLOADED, PROCESSING, DONE
}

enum class Resolution {
    ORIGINAL, P480, P720
}

fun newId() = Base58UUID().encode(UUID.randomUUID())

data class Meta(
    val id: String,
    val title: String,
    val fileName: String,
    val originalFileName: String,
    val ownerId: String,
    val public: Boolean,
    val tenantId: String,
    val resolution: Resolution = Resolution.ORIGINAL,
    val status: VideoState = VideoState.UPLOADED,
    val parentId: String? = null
)

@Configuration
class MetaStream(
    private val videoProcessor: VideoProcessor
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun consumeMetadata() = Consumer<Flux<Message<Meta>>> { stream ->
        stream.flatMap {
            mono {
                videoProcessor.processVideo(it.payload)
            }
        }.onErrorContinue { t, u ->
            logger.error { t.message }
        }.subscribeOn(Schedulers.single())
            .subscribe()
    }
}
