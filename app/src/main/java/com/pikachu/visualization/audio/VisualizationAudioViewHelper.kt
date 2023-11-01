package com.pikachu.visualization.audio

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner


class VisualizationAudioViewHelper(
    private val context: Context,
    private val attrs: AttributeSet?,
    private val defStyleAttr: Int = 0,
    private val defStyleRes: Int = 0,
    private val owner: VisualizationAudioView
) : IVisualizationAudioView {
    private var adapter: VisualizationAudioAdapter? = null
    private val audioVisualizerController: AudioVisualizerController by lazy {
        AudioVisualizerController.getInstance(context)
    }
    private var config: VisualizationAudioConfig = VisualizationAudioConfig()
    private var lifecycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            // 用户调用 start 后
            if (userOpenRun) stop()
        }

        override fun onPause(owner: LifecycleOwner) {
            // 用户调用 start 后
            if (userOpenRun) stop()
        }

        override fun onStop(owner: LifecycleOwner) {
            // 用户调用 start 后
            if (userOpenRun) stop()
        }

        override fun onResume(owner: LifecycleOwner) {
            // 用户调用 start 后
            if (userOpenRun) start()
        }
    }

    data class MeasureData(val measureWidth: Int, val measureHeight: Int)
    class VisualizationAudioConfig {

        // 是否镜像
        var isMirror = true

        // 跳过开头的第几个值  靠前值浮动大
        var countIndex: Int = 0

        // 是否平滑
        var isSmooth = true

        // 平滑区间
        var smoothInterval: Int = 3

        // 动画速度 ms    采样时间也同这个速度
        var animationSpeed: Int = 100

        // 差值器
        var timeInterpolator: TimeInterpolator = DecelerateInterpolator()

        // 最大值 值
        var maxRange: Float = 200f

        // 幅度
        var range: Float = 5f

        // 阻力 值越大加的越少
        var resistance: Float = 0f

        /**
         * 控件默认宽高
         */
        var defaultWidth: Int = 400
        var defaultHeight: Int = 400
    }


    private var isRunView = true
    private var userOpenRun = false
    private var isRun = true
    private var upData: FloatArray? = floatArrayOf()
    private var transform: FloatArray = floatArrayOf()
    private var animatorSet: AnimatorSet? = null


    init {
        owner.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        owner.viewTreeObserver.addOnGlobalLayoutListener {
            if (owner.visibility == View.VISIBLE) start()
            else stop()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        adapter ?: throw AudioException("Is not set adapter")
        val measureSpec = measureSpec(
            widthMeasureSpec,
            heightMeasureSpec,
            config.defaultWidth,
            config.defaultHeight
        )
        var count =
            adapter!!.onAudioViewMeasure(measureSpec.measureWidth, measureSpec.measureHeight, owner)
        count = count.coerceAtMost(1023) //  最大1024
        audioVisualizerController.setCount(if (config.isMirror) count / 2 + 1 else count)
    }

    override fun onDraw(canvas: Canvas) {
        if (isRun) {
            var model: FloatArray? = DataManipulationUtil.dataFilling(
                audioVisualizerController.getModel(),
                audioVisualizerController.getCount()
            )
            model = DataManipulationUtil.dataResistance(model, config.resistance)
            model = DataManipulationUtil.dataTransform(model, config.range, config.maxRange)
            if (config.isMirror) model = DataManipulationUtil.symmetry(model)
            if (config.isSmooth) {
                model = adapter!!.onAudioSmooth(model, owner)
            }
            adapter!!.onAnimatorStart(canvas, model, upData, owner)
            startAnimator(upData, model)
            upData = model
        }
        adapter!!.drawView(canvas, transform, owner)
        if (!isRunView) return
        owner.postInvalidate()
    }

    override fun measureSpec(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        defaultWidth: Int,
        defaultHeight: Int
    ): MeasureData {
        val measureWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val measureWidthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val measureHeightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val measureData =
            if (measureWidthMode == View.MeasureSpec.AT_MOST && measureHeightMode == View.MeasureSpec.AT_MOST) MeasureData(
                defaultWidth,
                defaultHeight
            )
            else if (measureWidthMode == View.MeasureSpec.AT_MOST) MeasureData(
                defaultWidth,
                measureHeight
            )
            else if (measureHeightMode == View.MeasureSpec.AT_MOST) MeasureData(
                measureWidth,
                defaultHeight
            )
            else MeasureData(measureWidth, measureHeight)
        try {
            View::class.java.getDeclaredMethod(
                "setMeasuredDimension",
                Int::class.java,
                Int::class.java
            ).apply {
                isAccessible = true
                invoke(owner, measureData.measureWidth, measureData.measureHeight)
            }
        } catch (_: Exception) {
        }
        return measureData
    }

    override fun startAnimator(to: FloatArray?, model: FloatArray?) {
        var toModel = to
        if (toModel == null || toModel.isEmpty() || model == null || model.isEmpty()) return
        if (toModel.size != model.size) toModel = DataManipulationUtil.dataFilling(to, model.size)
        if (animatorSet != null && animatorSet!!.isRunning) {
            animatorSet!!.cancel()
            animatorSet = null
        }
        val dd = FloatArray(model.size)
        animatorSet = AnimatorSet()
        val valueAnimators = arrayOfNulls<ValueAnimator>(model.size)
        for (i in model.indices) {
            val mLightWaveAnimator = ValueAnimator.ofFloat(toModel!![i], model[i])
            mLightWaveAnimator.addUpdateListener { animation: ValueAnimator ->
                dd[i] = animation.animatedValue as Float
                transform = dd
            }
            valueAnimators[i] = mLightWaveAnimator
        }
        animatorSet!!.playTogether(*valueAnimators)
        animatorSet!!.duration = config.animationSpeed.toLong()
        animatorSet!!.interpolator = config.timeInterpolator
        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isRun = false
            }

            override fun onAnimationEnd(animation: Animator) {
                isRun = true
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet!!.start()
    }

    override fun setAdapter(adapter: VisualizationAudioAdapter, context: Context?) {
        this.adapter = adapter
        adapter.onAdapterBind(owner)
        if (context != null && context is AppCompatActivity) {
            bindLifecycle(context.lifecycle)
        }
    }

    override fun start() {
        audioVisualizerController.start()
        audioVisualizerController.setCountIndex(config.countIndex)
        isRunView = true
        userOpenRun = true
        owner.postInvalidate()
    }

    override fun stop() {
        audioVisualizerController.stop()
        isRunView = false
        owner.postInvalidate()
    }

    override fun setVisualizationAudioConfig(config: VisualizationAudioConfig) {
        this.config = config
    }

    override fun getVisualizationAudioConfig() = config
    override fun bindLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
    }

}