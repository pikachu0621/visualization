package com.pkpk.zaudio.view

import android.content.Context
import android.graphics.Canvas
import androidx.lifecycle.Lifecycle


interface IVisualizationAudioView {


    /**
     * 数据测量
     */
    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)

    /**
     * 画数据
     */
    fun onDraw(canvas: Canvas)



    /**
     * 数据测量工具
     */
    fun measureSpec(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        defaultWidth: Int = 400,
        defaultHeight: Int = 400
    ): VisualizationAudioViewHelper.MeasureData



    /**
     * 启动 动画
     *
     * 高于上一个值就快速上升
     * 低于上一个值就不管
     * 当上升到高值后 慢速下降
     */
    fun startAnimator(to: FloatArray?, model: FloatArray?)



    /**
     * 设置适配器 并绑定 lifecycle
     */
    fun setAdapter(adapter: VisualizationAudioAdapter, context: Context? = null)



    /**
     * 开始
     */
    fun start()

    /**
     * 停止
     */
    fun stop()


    /**
     * 设置配置
     */
    fun setVisualizationAudioConfig(config: VisualizationAudioViewHelper.VisualizationAudioConfig)

    /**
     * 获取配置
     */
    fun getVisualizationAudioConfig(): VisualizationAudioViewHelper.VisualizationAudioConfig


    /**
     * 绑定生命周期
     */
    fun bindLifecycle(lifecycle: Lifecycle)

}