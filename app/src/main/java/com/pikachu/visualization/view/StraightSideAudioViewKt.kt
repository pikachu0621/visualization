package com.pikachu.visualization.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.pikachu.visualization.audio.BaseAudioView

/**
 * @author pkpk.run
 * @project 音乐可视化
 * @package com.pikachu.visualization
 * @date 2022/3/17
 * @description 略
 */
class StraightSideAudioViewKt : BaseAudioView {


    private val DEFAULT_WIDTH = 400
    private val DEFAULT_HEIGHT = 200
    private val linWidth = 40 // 宽
    private val clearance = 0 // 间隙
    private val minHeight = 10 // 没有音乐时高度
    private var mPaint = Paint()
    private var t1 = 0
    private var path: Path? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        path = Path()

        setMirror(true) // 是否镜像
        setSmooth(true) // 是否平滑
        setSmoothInterval(3) // 平滑窗口
        setCountIndex(0) // 跳过开头的第几个值
        setAnimationSpeed(80) // 动画速度
        setTimeInterpolator(LinearInterpolator()) // 动画差值器
        setRange(7f) // 幅度
    }


    override fun onAudioMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Int {
        val ints = reMeasure(
            widthMeasureSpec,
            heightMeasureSpec,
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT
        )
        val width = ints[0]
        val height = ints[1]
        t1 = linWidth + clearance
        val count = width / t1
        setMaxRange(height.toFloat())
        return count + 1 /*/ 3*/

    }

    override fun drawView(canvas: Canvas?, transform: FloatArray?) {

        var j = 0
        for ((index, value) in transform!!.withIndex()) {
            val fl: Float = if (value <= minHeight) {
                (height - minHeight).toFloat()
            } else {
                height - value
            }
            val tt = t1 * j
            val mShader: Shader = LinearGradient(
                (tt + (linWidth / 2)).toFloat(),
                height.toFloat(),
                (tt + (linWidth / 2)).toFloat(),
                fl,
                intArrayOf(
                    0xFF000000.toInt(),
                   /* Color.TRANSPARENT,*/
                    /*0xFFFF0000.toInt(),*/
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.REPEAT
            )
            mPaint.shader = mShader

            //mPaint.setShadowLayer(0.2F, 0.2F, 0.2F, 0xFFFF0000.toInt())
            canvas!!.drawRect(
                tt.toFloat(),
                fl,
                (tt + linWidth).toFloat(),
                height.toFloat(),
                mPaint
            )
            j ++
        }
    }
}