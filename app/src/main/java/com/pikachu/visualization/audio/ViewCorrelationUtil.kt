package com.pikachu.visualization.audio

import kotlin.math.cos
import kotlin.math.sin

object ViewCorrelationUtil {

    /**
     * 计算圆上点坐标
     *
     * @param centerX 圆心 x
     * @param centerY 圆心 y
     * @param radius  半径
     * @param angle   角度
     * @return { 0 =  x, 1 =  y }
     */
    fun computedCenter(centerX: Float, centerY: Float, radius: Float, angle: Float): FloatArray {
        val pi = (angle - 90) * Math.PI / 180
        val x = centerX + radius * cos(pi).toFloat()
        val y = centerY + radius * sin(pi).toFloat()
        return  floatArrayOf(x, y )
    }



}