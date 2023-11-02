package com.pikachu.visualization

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.pikachu.visualization.databinding.UiZaudioControllerBinding
import com.pkpk.zaudio.view.VisualizationAudioView
import com.pkpk.zaudio.view.VisualizationAudioViewHelper

/**
 * 控制器
 */
class ZAudiocontroller(
    private val activity: Activity,
    private val root: FrameLayout,
    private val visView: VisualizationAudioView,
) {
    private val config = visView.getVisualizationAudioConfig()
    private val configCopy = config.copy()
    private val binding by lazy {
        UiZaudioControllerBinding.inflate(
            LayoutInflater.from(activity),
            root,
            false
        )
    }
    init {
        root.removeAllViews()
        root.addView(binding.root)
        loadView(binding)
    }

    private fun loadView(binding: UiZaudioControllerBinding){
        configBindView(binding)

        binding.dataReset.setOnClickListener {
            configBindView(binding, configCopy)
        }


        binding.on.setOnClickListener {
            it.visibility = View.GONE
            binding.constraintLayout.visibility = View.VISIBLE
        }

        binding.constraintLayout.setOnClickListener {
            it.visibility = View.GONE
            binding.on.visibility = View.VISIBLE
        }

        binding.landscape.setOnClickListener {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        binding.portraitScreen.setOnClickListener {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        binding.sw1.setOnCheckedChangeListener { _, isChecked ->
            config.isMirror = isChecked
            renewalConfig()
        }

        binding.sw2.setOnCheckedChangeListener { _, isChecked ->
            config.isSmooth = isChecked
            renewalConfig()
        }

        binding.sw2.setOnCheckedChangeListener { _, isChecked ->
            config.isSmooth = isChecked
            renewalConfig()
        }

        ///
        binding.seekBar1.setOnSeekBarChangeListener(SeekBarComputeChangeListener(0F, 20F) {
            config.smoothInterval = it
            replaceText(binding.seekBar1t, it)
            renewalConfig()
        })

        binding.seekBar2.setOnSeekBarChangeListener(SeekBarComputeChangeListener {
            config.countIndex = it.toInt()
            replaceText(binding.seekBar2t, it)
            renewalConfig()
        })

        binding.seekBar3.setOnSeekBarChangeListener(
            SeekBarComputeChangeListener(
                newMax = 300F,
                oldMax = 300F
            ) {
                config.animationSpeed = it.toInt()
                replaceText(binding.seekBar3t, it)
                renewalConfig()
            })

        binding.seekBar4.setOnSeekBarChangeListener(
            SeekBarComputeChangeListener(
                newMax = 1000F,
                oldMax = 1000F
            ) {
                config.maxRange = it
                replaceText(binding.seekBar4t, it)
                renewalConfig()
            })

        binding.seekBar5.setOnSeekBarChangeListener(SeekBarComputeChangeListener(0F, 20F) {
            config.range = it
            replaceText(binding.seekBar5t, it)
            renewalConfig()
        })

        binding.seekBar6.setOnSeekBarChangeListener(SeekBarComputeChangeListener {
            config.resistance = it
            replaceText(binding.seekBar6t, it)
            renewalConfig()
        })
    }


    private fun renewalConfig() {
        visView.setVisualizationAudioConfig(config)
    }


    private fun configBindView(binding: UiZaudioControllerBinding, config: VisualizationAudioViewHelper.VisualizationAudioConfig? = null) {
        val cng = config ?: this.config
        binding.sw1.isChecked = cng.isMirror
        binding.sw2.isChecked = cng.isSmooth
        binding.seekBar1.setBarVar(binding.seekBar1t, cng.smoothInterval, 0F, 20F)
        binding.seekBar2.setBarVar(binding.seekBar2t, cng.countIndex.toFloat())
        binding.seekBar3.setBarVar(binding.seekBar3t, cng.animationSpeed.toFloat(),  newMax = 300F, oldMax = 300F)
        binding.seekBar4.setBarVar(binding.seekBar4t, cng.maxRange,   newMax = 1000F, oldMax = 1000F)
        binding.seekBar5.setBarVar(binding.seekBar5t, cng.range,   0F, 20F)
        binding.seekBar6.setBarVar(binding.seekBar6t, cng.resistance)
    }


    private fun SeekBar.setBarVar(
        textView: TextView,
        originalValue: Float,
        newMin: Float = 0F,
        newMax: Float = 100F,
        oldMin: Float = 0F,
        oldMax: Float = 100F
    ) {
        this.max = oldMax.toInt()
        this.progress =
            ((originalValue - newMin) / (newMax - newMin) * (oldMax - oldMin) + oldMin).toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.min = oldMin.toInt()
        }
        replaceText(textView, progress.toFloat())
        // replace("{num}", "($progress)")
    }

    private fun replaceText(textView: TextView, varText: Float){
        textView.text = "(.+?) - (.+)".toRegex().replace(textView.text.toString()) { matchResult ->
            val firstXX = matchResult.groupValues[1]
            "$firstXX - ${"%.2f".format(varText)}"
        }
    }


    private class SeekBarComputeChangeListener(
        private val newMin: Float = 0F,
        private val newMax: Float = 100F,
        private val oldMin: Float = 0F,
        private val oldMax: Float = 100F,
        private val onComputeProgressChanged: (trueProgress: Float) -> Unit
    ) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                onComputeProgressChanged(
                    ((progress - oldMin) / (oldMax - oldMin) * (newMax - newMin) + newMin)
                )
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

}