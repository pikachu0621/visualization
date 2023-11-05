package com.pkpk.zaudio.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.animation.LinearInterpolator
import com.pkpk.zaudio.view.ViewCorrelationUtil
import com.pkpk.zaudio.view.VisualizationAudioAdapter
import com.pkpk.zaudio.view.VisualizationAudioView

class AnnularAdapter:  VisualizationAudioAdapter() {

    private val linWidth = 4 // 宽
    private val minHeight = 5 // 没有音乐时高度
    private val size = 100 // 大小

    private var mPaint = Paint()
    private var count = 180
    private var roundDia = 0
    private var roundX = 0f
    private var roundY = 0f
    private var radius = 0f
    private var minRadius = 0f

    init {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = linWidth.toFloat()
        mPaint.style = Paint.Style.STROKE
    }

    override fun onAdapterBind(view: VisualizationAudioView) {
        val config = view.getVisualizationAudioConfig()
        config.isMirror = true
        config.isSmooth = true
        config.smoothInterval = 2F
        config.countIndex = 0
        // config.animationSpeed = 100
        config.timeInterpolator = LinearInterpolator()
        config.range = 5F
        config.resistance = 4F
    }

    override fun onAudioViewMeasure(measureWidth: Int, measureHeight: Int, view: VisualizationAudioView): Int {
        roundDia = measureWidth.coerceAtMost(measureHeight)
        radius = roundDia / 2f
        roundX = measureWidth / 2f
        roundY = measureHeight / 2f
        minRadius = radius / 2 + size
        view.getVisualizationAudioConfig().maxRange = radius
        return count - 2
    }

    override fun drawView(canvas: Canvas, transform: FloatArray, view: VisualizationAudioView) {
        var an = 0
        for ((index, value) in transform.withIndex()) {
            var oneVar = value / 2

            if (oneVar <= minHeight) {
                oneVar = minHeight.toFloat()
            }
            val round1 = ViewCorrelationUtil.computedCenter(roundX, roundY, minRadius, (180 - an).toFloat())
            val round2 = ViewCorrelationUtil.computedCenter(round1[0], round1[1], oneVar, -an.toFloat())
            val round3 = ViewCorrelationUtil.computedCenter(round1[0], round1[1], oneVar, (180 - an).toFloat())

            val mShader: Shader = LinearGradient(
                round2[0],
                round2[1],
                round3[0],
                round3[1],
                intArrayOf(0xFFFF69B4.toInt(), Color.TRANSPARENT, 0xFF98FB98.toInt()),
                null, Shader.TileMode.REPEAT
            )
            mPaint.shader = mShader

            canvas.drawLine(
                round2[0],
                round2[1],
                round3[0],
                round3[1],
                mPaint
            )
            an  += 2
        }
    }
}