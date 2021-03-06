// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.mediaanalyzer

import org.junit.jupiter.api.Tag
import se.svt.oss.mediaanalyzer.Assertions.assertThat
import se.svt.oss.mediaanalyzer.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import se.svt.oss.mediaanalyzer.file.AudioFile
import se.svt.oss.mediaanalyzer.file.VideoFile

@Tag("integrationTest")
class MediaAnalyzerIntegrationTest {

    @Test
    fun testNonExistingFile() {
        val file = "path/to/nonexisting"
        assertThatThrownBy { MediaAnalyzer().analyze(file) }
            .hasMessage("ffprobe failed for $file: No such file or directory")
    }

    @Test
    fun testVideoFile() {
        val file = javaClass.getResource("/test.mp4").file
        val videoFile = MediaAnalyzer().analyze(file, true) as VideoFile
        println("videoFile is: $videoFile")
        assertThat(videoFile)
            .hasFormat("MPEG-4")
            .hasDuration(10.016)
            .hasOverallBitrate(2424115)
            .hasFileSize(3034992)

        assertThat(videoFile.file).endsWith("/test.mp4")

        assertThat(videoFile.videoStreams).hasSize(1)
        assertThat(videoFile.highestBitrateVideoStream)
            .isNotInterlaced
            .hasFormat("AVC")
            .hasCodec("h264")
            .hasProfile("High 4:2:2")
            .hasLevel("5.1")
            .hasWidth(1920)
            .hasHeight(1080)
            .hasSampleAspectRatio("1:1")
            .hasDisplayAspectRatio("16:9")
            .hasPixelFormat("yuv422p10le")
            .hasFrameRate("25/1")
            .hasDuration(10.0)
            .hasBitrate(2037046)
            .hasBitDepth(10)
            .hasNumFrames(250)
            .hasTransferCharacteristics(null)

        assertThat(videoFile.audioStreams).hasSize(1)
        assertThat(videoFile.audioStreams)
            .allSatisfy {
                assertThat(it)
                    .hasFormat("AC-3")
                    .hasCodec("ac3")
                    .hasDuration(10.01)
                    .hasSamplingRate(48000)
                    .hasChannels(6)
            }
        assertThat(videoFile.audioStreams)
            .extracting("bitrate")
            .containsExactly(
                384000L
            )
    }

    @Test
    fun testInterlaced() {
        val file = javaClass.getResource("/test_interlaced_anamorphic.dv").file

        val videoFile = MediaAnalyzer().analyze(file, true) as VideoFile

        assertThat(videoFile.highestBitrateVideoStream)
            .isInterlaced
            .hasSampleAspectRatio("16:15")
            .hasDisplayAspectRatio("4:3")
    }

    @Test
    fun testAudio() {
        val file = javaClass.getResource("/test_audio_file.mp4").file

        val audioFile = MediaAnalyzer()
            .analyze(file) as AudioFile

        assertThat(audioFile)
            .hasFormat("MPEG-4")
            .hasOverallBitrate(132016)
            .hasDuration(2.643)

        assertThat(audioFile.audioStreams).hasSize(1)
        assertThat(audioFile.audioStreams.first())
            .hasFormat("AAC")
            .hasCodec("aac")
            .hasChannels(2)
            .hasDuration(2.621)
            .hasSamplingRate(48000)
            .hasBitrate(128104)
    }
}
