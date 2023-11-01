package com.pikachu.visualization.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.view.animation.LinearInterpolator
import com.pikachu.visualization.audio.ViewCorrelationUtil
import com.pikachu.visualization.audio.VisualizationAudioAdapter
import com.pikachu.visualization.audio.VisualizationAudioView

class AnnularAdapter1:  VisualizationAudioAdapter() {

    private val linWidth = 4 // 宽
    private val minHeight = 5 // 没有音乐时高度
    private val size = 60 // 大小

    private var mPaint = Paint()
    private var path = Path()
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
        mPaint.isDither = true
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onAdapterBind(view: VisualizationAudioView) {
        val config = view.getVisualizationAudioConfig()
        config.isMirror = true
        config.isSmooth = false
        config.smoothInterval = 2
        config.countIndex = 0
        config.animationSpeed = 80
        config.timeInterpolator = LinearInterpolator()
        config.range = 5F
    }

    override fun onAudioViewMeasure(measureWidth: Int, measureHeight: Int, view: VisualizationAudioView): Int {
        roundDia = measureWidth.coerceAtMost(measureHeight)
        radius = roundDia / 2f
        roundX = measureWidth / 2f
        roundY = measureHeight / 2f
        minRadius = radius / 2 + size
        view.getVisualizationAudioConfig().maxRange = radius
        return count + 2
    }

    override fun drawView(canvas: Canvas, transform: FloatArray, view: VisualizationAudioView) {
        var an = 0

        var upx = 0F
        var upy = 0F

        for ((index, value) in transform!!.withIndex()) {
            var oneVar = value / 2

            if (oneVar <= minHeight) {
                oneVar = minHeight.toFloat()
            }
            val round0 = ViewCorrelationUtil.computedCenter(roundX, roundY, minRadius, (180 - an).toFloat())
            val round1 = ViewCorrelationUtil.computedCenter(round0[0], round0[1], oneVar, (180 - an).toFloat())

            val round2 = ViewCorrelationUtil.computedCenter(roundX, roundY, minRadius - 10, (180 - an).toFloat())
            val round3 = ViewCorrelationUtil.computedCenter(round2[0], round2[1], oneVar / 2, -an.toFloat())

            if (index == 0)
                path.moveTo(round3[0], round3[1])
            else
                path.quadTo(upx, upy, round3[0], round3[1])
            upx = round3[0];
            upy = round3[1];


             canvas.drawLine(
                 round0[0],
                 round0[1],
                 round1[0],
                 round1[1],
                 mPaint
             )
            an += 2
        }

        mPaint.strokeWidth = 3F
        path.close()
        // canvas.drawCircle(roundX, roundY, minRadius - 10, mPaint)
        canvas.drawPath(path, mPaint)
        path.reset()
        mPaint.strokeWidth = linWidth.toFloat()
    }
}