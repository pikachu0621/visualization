package com.pkpk.zaudio.view

import android.graphics.Canvas

abstract class VisualizationAudioAdapter {

    /**
     * 开始时 可设置配置项
     */
    open fun onAdapterBind(view: VisualizationAudioView){

    }

    /**
     *
     * 测量View 数据
     *
     * @return 需要多少条数据源
     */
    abstract fun onAudioViewMeasure(measureWidth: Int, measureHeight: Int, view: VisualizationAudioView): Int

    /**
     * 在一个动画执行时
     *
     * @param canvas 画布
     * @param model  当前数据
     * @param upData 上次数据
     */
    open  fun onAnimatorStart(canvas: Canvas, model: FloatArray?, upData: FloatArray?, view: VisualizationAudioView){

    }


    /**
     * 平滑处理函数
     */
    open fun onAudioSmooth(model: FloatArray?, view: VisualizationAudioView): FloatArray?{
        var models = model
        models = DataManipulationUtil.applySmoothGaussian(models, view.getVisualizationAudioConfig().smoothInterval.toFloat())
        return models
    }




    /**
     * 画布
     *
     * @param canvas    画布
     * @param transform 处理后的FFT数据
     */
    abstract fun drawView(canvas: Canvas, transform: FloatArray, view: VisualizationAudioView)

}