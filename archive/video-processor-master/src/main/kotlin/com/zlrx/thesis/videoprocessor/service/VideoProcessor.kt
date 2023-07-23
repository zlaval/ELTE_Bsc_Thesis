package com.zlrx.thesis.videoprocessor.service

import com.zakgof.velvetvideo.IRawPacket
import com.zakgof.velvetvideo.impl.VelvetVideoLib
import com.zlrx.thesis.videoprocessor.stream.Meta
import com.zlrx.thesis.videoprocessor.stream.newId
import io.micrometer.observation.annotation.Observed
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.File

@Service
class VideoProcessor {

    private val logger = KotlinLogging.logger {}

    @Observed(
        name = "process.video",
        contextualName = "process.video",
    )
    suspend fun processVideo(metaData: Meta) {
        logger.info { metaData }

        val id = newId()
        val fileName = "$id.mp4"

        logger.info { "Start to process video ${metaData.fileName}. Create new video $fileName" }

        val lib = VelvetVideoLib.getInstance()

        val input = File("/data/${metaData.fileName}")
        val output = File("/data/$fileName")

        lib.demuxer(input).use { demuxer ->
            val decoders = demuxer.streams()
            val muxerBuilder = lib.muxer("mp4")

            val mapDecoderToRemuxer = mutableMapOf<Int, Int>()

            decoders.forEachIndexed { index, decoder ->
                muxerBuilder.remuxer(lib.remuxer(decoder))
                mapDecoderToRemuxer[decoder.index()] = index
            }

            muxerBuilder.build(output).use { muxer ->
                var raw: IRawPacket? = demuxer.nextRawPacket()

                while (raw != null) {
                    val index = mapDecoderToRemuxer[raw.streamIndex()]
                    if (index != null) {
                        muxer.remuxer(index).writeRaw(raw.bytes())
                    }
                    raw = demuxer.nextRawPacket()
                }
            }
        }
    }
}
