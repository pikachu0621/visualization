package com.pkpk.zaudio.view

import android.Manifest.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.hypot


class AudioVisualizerController private constructor(private val context: Context) {

    private var visualizer: Visualizer? = null
    private var count = 0
    private var countIndex = 1 // 跳过开头的第几个值
    private var fftListener: ((model: FloatArray?) -> Unit)? = null
    private var model: FloatArray? = null


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var audioVisualizerController: AudioVisualizerController? = null
        private var audioSession: Int = 0
        const val SYS_AUDIO_SESSION = 0
        fun getInstance(context: Context): AudioVisualizerController {
            if (audioVisualizerController == null) {
                synchronized(AudioVisualizerController::class.java) {
                    if (audioVisualizerController == null) audioVisualizerController = AudioVisualizerController(context)
                }
            }
            return audioVisualizerController!!
        }

        fun checkAudioPermission(context: Context) =
            ContextCompat.checkSelfPermission(context, permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED


        /**
         * 设置 audioSession 系统为 0  app  为 mediaPlayer.getAudioSessionId()
         */
        fun setAudioSession(audioSession: Int){
            this.audioSession = audioSession
        }

        fun getAudioSession() = this.audioSession
    }


    // 获取当前一组数据
    fun getModel(): FloatArray? {
        if (count <= 0 || model == null || model!!.size < count + countIndex) return model
        val floats = FloatArray(count)
        System.arraycopy(model!!, countIndex, floats, 0, floats.size)
        return floats
    }


    fun start() {
        if (!checkAudioPermission(context)) return
        visualizer?.release()
        visualizer = Visualizer(audioSession)
        visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[1]
        visualizer!!.setDataCaptureListener(object : OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer,
                waveform: ByteArray,
                samplingRate: Int
            ) {
            }

            override fun onFftDataCapture(
                visualizer: Visualizer,
                fft: ByteArray,
                samplingRate: Int
            ) {
                val f = fft.size / 2
                model = FloatArray(f + 1)
                model!![0] = abs(fft[1].toInt()).toFloat()
                var j = 1
                if (count <= 0 || count > f) count = f
                var i = 2
                while (i < count * 2) {
                    model!![j] = hypot(fft[i].toDouble(), fft[i + 1].toDouble()).toFloat()
                    i += 2
                    j++
                    model!![j] = abs(model!![j])
                }
                fftListener?.let { it((model)) }
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true)
        visualizer!!.enabled = true
    }


    fun stop() {
        visualizer ?.release()
        audioVisualizerController = null
    }

    // 设置监听
    fun setFftListener(fftListener: (model: FloatArray?) -> Unit) {
        this.fftListener = fftListener
    }

    fun getCountIndex() = countIndex

    // 跳过开头的第几个值
    fun setCountIndex(countIndex: Int) {
        this.countIndex = countIndex.coerceAtLeast(0)
    }

    // 设置长度
    fun setCount(count: Int) {
        this.count = count
    }

    fun getCount() = count
}