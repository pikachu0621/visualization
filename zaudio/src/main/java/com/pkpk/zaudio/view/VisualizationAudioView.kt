package com.pkpk.zaudio.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.Lifecycle


open class VisualizationAudioView : View, IVisualizationAudioView {

    private lateinit var visualizationAudioViewHelper: VisualizationAudioViewHelper

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        context ?: return
        visualizationAudioViewHelper =
            VisualizationAudioViewHelper(context, attrs, defStyleAttr, owner = this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (context == null) setMeasuredDimension(0, 0)
        visualizationAudioViewHelper.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        visualizationAudioViewHelper.onDraw(canvas)
    }

    override fun measureSpec(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        defaultWidth: Int,
        defaultHeight: Int
    ): VisualizationAudioViewHelper.MeasureData =
        visualizationAudioViewHelper.measureSpec(
            widthMeasureSpec,
            heightMeasureSpec,
            defaultWidth,
            defaultHeight
        )

    override fun startAnimator(to: FloatArray?, model: FloatArray?) =
        visualizationAudioViewHelper.startAnimator(to, model)

    override fun setAdapter(adapter: VisualizationAudioAdapter, context: Context?) =
        visualizationAudioViewHelper.setAdapter(adapter, context)

    override fun start() = visualizationAudioViewHelper.start()

    override fun stop() = visualizationAudioViewHelper.stop()

    override fun setVisualizationAudioConfig(config: VisualizationAudioViewHelper.VisualizationAudioConfig) =
        visualizationAudioViewHelper.setVisualizationAudioConfig(config)

    override fun getVisualizationAudioConfig(): VisualizationAudioViewHelper.VisualizationAudioConfig =
        visualizationAudioViewHelper.getVisualizationAudioConfig()

    override fun bindLifecycle(lifecycle: Lifecycle) =
        visualizationAudioViewHelper.bindLifecycle(lifecycle)


}