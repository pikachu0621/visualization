package com.pkpk.zaudio.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.animation.LinearInterpolator
import com.pkpk.zaudio.view.VisualizationAudioAdapter
import com.pkpk.zaudio.view.VisualizationAudioView

class StraightSideAdapter: VisualizationAudioAdapter() {

    private val linWidth = 15 // 宽
    private val clearance = 10 // 间隙
    private val minHeight = 10 // 没有音乐时高度
    private var mPaint = Paint()
    private var t1 = 0

    init {
        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onAdapterBind(view: VisualizationAudioView) {
        val config = view.getVisualizationAudioConfig()
        config.isMirror = true
        config.isSmooth = true
        config.smoothInterval = 1.5F
        config.countIndex = 3
        // config.animationSpeed = 110
        config.timeInterpolator = LinearInterpolator()
        config.range = 5F
        config.resistance = 4F
    }


    override fun onAudioViewMeasure(
        measureWidth: Int,
        measureHeight: Int,
        view: VisualizationAudioView
    ): Int {
        t1 = linWidth + clearance
        val count = measureWidth / t1
        view.getVisualizationAudioConfig().maxRange = measureHeight.toFloat()
        return count /*/ 3*/
    }

    override fun drawView(
        canvas: Canvas,
        transform: FloatArray,
        view: VisualizationAudioView
    ) {
        var j = 0
        /* val path = Path()
         var x = 0F
         var y = 0F*/
        for ((index, value) in transform.withIndex()) {
            val fl: Float = if (value <= minHeight) {
                (view.height - minHeight).toFloat()
            } else {
                view.height - value
            }
            val tt = t1 * j
            val mShader: Shader = LinearGradient(
                (tt + (linWidth / 2)).toFloat(),
                view.height.toFloat(),
                (tt + (linWidth / 2)).toFloat(),
                fl,
                intArrayOf(
                    0xFFD9AFD9.toInt(),
                    0xFF97D9E1.toInt(),
                    Color.TRANSPARENT
                    /*                       0xFFFF0000.toInt(),
                                           Color.TRANSPARENT*/
                ),
                null,
                Shader.TileMode.MIRROR
            )
            mPaint.shader = mShader


            /*
                        if (index == 0){
                            path.moveTo((tt + (linWidth / 2)).toFloat(), fl)
                            x = (tt + (linWidth / 2)).toFloat()
                            y = fl
                        }

                        if (index % 2 == 0) {
                            path.quadTo(x, y, (tt + (linWidth / 2)).toFloat(), fl)
                        } else  {
                            x = (tt + (linWidth / 2)).toFloat()
                            y = fl
                        }*/



            // mPaint.setShadowLayer(0.2F, 0.2F, 0.2F, 0xFFFF0000.toInt())
            canvas.drawRoundRect(
                tt.toFloat(),
                fl,
                (tt + linWidth).toFloat(),
                view.height.toFloat(),
                linWidth / 2F,linWidth / 2F,
                mPaint
            )
            j ++
        }
        // canvas!!.drawPath(path, mPaint)
    }

    /*override fun onAudioSmooth(model: FloatArray?, view: VisualizationAudioView): FloatArray? {
        var models = model
        models = DataManipulationUtil.applyHighPassFilterToData(models, 70F, 100F)
        return models
    }*/
}