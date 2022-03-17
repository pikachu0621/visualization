package com.pikachu.visualization.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.pikachu.visualization.audio.BaseAudioView

/**
 * @author pkpk.run
 * @project 音乐可视化
 * @package com.pikachu.visualization.view
 * @date 2022/3/17
 * @description 略
 */
class AnnularAudioViewKt : BaseAudioView {


    private val DEFAULT_WIDTH = 400
    private val DEFAULT_HEIGHT = 400
    private val linWidth = 4 // 宽
    private val minHeight = 5 // 没有音乐时高度
    private val size = 100 // 大小


    private var mPaint = Paint()
    private var path: Path? = null
    private var count = 180
    private var anWidth = 0
    private var anHeight = 0
    private var roundDia = 0
    private var roundX = 0f
    private var roundY = 0f
    private var radius = 0f
    private var minRadius = 0f


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = linWidth.toFloat()
        mPaint.style = Paint.Style.STROKE


        setMirror(true) // 是否镜像
        setSmooth(true) // 是否平滑
        setSmoothInterval(6) // 平滑窗口
        setCountIndex(0) // 跳过开头的第几个值
        setAnimationSpeed(100) // 动画速度
        setTimeInterpolator(LinearInterpolator()) // 动画差值器
        setRange(7f) // 幅度
        //setResistance(1.2F); // 阻力
    }


    override fun onAudioMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Int {
        val ints = reMeasure(
            widthMeasureSpec,
            heightMeasureSpec,
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT
        )
        anWidth = ints[0]
        anHeight = ints[1]
        roundDia = anWidth.coerceAtMost(anHeight)
        radius = roundDia / 2f
        roundX = anWidth / 2f
        roundY = anHeight / 2f
        minRadius = radius / 2 + size
        setMaxRange(radius)
        return count
    }

    override fun drawView(canvas: Canvas?, transform: FloatArray?) {


        var an = 0
        for ((index, value) in transform!!.withIndex()) {
            var oneVar = value / 2

            if (oneVar <= minHeight) {
                oneVar = minHeight.toFloat()
            }
            val round1 = computeCircleXY(roundX, roundY, minRadius, (180 - an).toFloat())
            val round2 = computeCircleXY(round1[0], round1[1], oneVar, -an.toFloat())
            val round3 = computeCircleXY(round1[0], round1[1], oneVar, (180 - an).toFloat())

            val mShader: Shader = LinearGradient(
                round2[0],
                round2[1],
                round3[0],
                round3[1],
                intArrayOf(0xFFFF69B4.toInt(), Color.TRANSPARENT, 0xFF98FB98.toInt()),
                null, Shader.TileMode.REPEAT
            )
            mPaint.shader = mShader

            canvas!!.drawLine(
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