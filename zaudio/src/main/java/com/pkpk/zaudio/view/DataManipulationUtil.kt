package com.pkpk.zaudio.view

import kotlin.math.exp


/**
 * 数据操作
 * 包括 数据拟合平滑算法
 */
object DataManipulationUtil {


    /**
     * 三次五阶平滑算法
     *
     * @param `data` 待处理的数据
     * @return 处理后得到的数据
     */
    fun applySmoothCubic5(`data`: FloatArray?): FloatArray? {
        if (`data` == null || `data`.size < 5) return `data`
        val len = `data`.size
        val out = FloatArray(len)
        out[0] = (69 * `data`[0] + 4 * `data`[1] - 6 * `data`[2] + 4 * `data`[3] - `data`[4]) / 70
        out[1] = (2 * `data`[0] + 27 * `data`[1] + 12 * `data`[2] - 8 * `data`[3] + 2 * `data`[4]) / 35
        for (i in 2..len - 3) out[i] = (-3 * (`data`[i - 2] + `data`[i + 2]) + 12 * (`data`[i - 1] + `data`[i + 1]) + 17 * `data`[i]) / 35
        out[len - 2] = (2 * `data`[len - 5] - 8 * `data`[len - 4] + 12 * `data`[len - 3] + 27 * `data`[len - 2] + 2 * `data`[len - 1]) / 35
        out[len - 1] = (-`data`[len - 5] + 4 * `data`[len - 4] - 6 * `data`[len - 3] + 4 * `data`[len - 2] + 69 * `data`[len - 1]) / 70
        return out
    }

    /**
     * 三次七阶平滑算法
     *
     * @param `data` 待处理的数据
     * @return 处理后得到的数据
     */
    fun applySmoothCubic7(`data`: FloatArray?): FloatArray? {
        if (`data` == null || `data`.size < 7) return `data`
        val len = `data`.size
        val out = FloatArray(len)
        out[0] = (39 * `data`[0] + 8 * `data`[1] - 4 * `data`[2] - 4 * `data`[3] + 1 * `data`[4] + 4 * `data`[5] - 2 * `data`[6]) / 42
        out[1] = (8 * `data`[0] + 19 * `data`[1] + 16 * `data`[2] + 6 * `data`[3] - 4 * `data`[4] - 7 * `data`[5] + 4 * `data`[6]) / 42
        out[2] = (-4 * `data`[0] + 16 * `data`[1] + 19 * `data`[2] + 12 * `data`[3] + 2 * `data`[4] - 4 * `data`[5] + 1 * `data`[6]) / 42
        for (i in 3..len - 4) out[i] = (-2 * (`data`[i - 3] + `data`[i + 3]) + 3 * (`data`[i - 2] + `data`[i + 2]) + 6 * (`data`[i - 1] + `data`[i + 1]) + 7 * `data`[i]) / 21
        out[len - 3] = (-4 * `data`[len - 1] + 16 * `data`[len - 2] + 19 * `data`[len - 3] + 12 * `data`[len - 4] + 2 * `data`[len - 5] - 4 * `data`[len - 6] + 1 * `data`[len - 7]) / 42
        out[len - 2] = (8 * `data`[len - 1] + 19 * `data`[len - 2] + 16 * `data`[len - 3] + 6 * `data`[len - 4] - 4 * `data`[len - 5] - 7 * `data`[len - 6] + 4 * `data`[len - 7]) / 42
        out[len - 1] = (39 * `data`[len - 1] + 8 * `data`[len - 2] - 4 * `data`[len - 3] - 4 * `data`[len - 4] + 1 * `data`[len - 5] + 4 * `data`[len - 6] - 2 * `data`[len - 7]) / 42
        return out
    }

    /**
     * 三次七阶平滑算法
     *
     * @param `data` 待处理的数据
     * @param frequency 计算几次
     * @return 处理后得到的数据
     */
    fun applySmoothCubic7(`data`: FloatArray?, frequency: Int): FloatArray? {
        var dataSmooth: FloatArray? = `data`
        for (i in 0..frequency) {
            dataSmooth = applySmoothCubic7(dataSmooth)
        }
        return dataSmooth
    }

    /**
     * 三次五阶平滑算法
     *
     * @param `data` 待处理的数据
     * @param frequency 计算几次
     * @return 处理后得到的数据
     */
    fun applySmoothCubic5(`data`: FloatArray?, frequency: Int): FloatArray? {
        var dataSmooth: FloatArray? = `data`
        for (i in 0..frequency) {
            dataSmooth = applySmoothCubic5(dataSmooth)
        }
        return dataSmooth
    }

    /**
     * 滑动均值算法
     *
     * @param `data`     源数据
     * @param interval 窗口
     * @return FloatArray
     */
    fun applySmoothMovingAverage(`data`: FloatArray?, interval: Int): FloatArray? {
        if (`data` == null || `data`.isEmpty() || interval <= 0) return `data`
        val fst = FloatArray(`data`.size)
        for (i in `data`.indices) {
            var `var` = 0f
            if (i + interval < `data`.size) {
                for (j in 0 until interval) `var` += `data`[i + j]
                fst[i] = `var` / interval
            } else {
                val i1 = `data`.size - i
                for (j in 0 until i1) `var` += `data`[i + j]
                fst[i] = `var` / i1
            }
        }
        return fst
    }

    /**
     * 高斯平滑算法
     * sigma 1 - 100
     */
    fun applySmoothGaussian(ttf: FloatArray?, sigma: Float): FloatArray? {
        if (ttf == null || ttf.isEmpty()) return ttf
        val size = ttf.size
        val smoothedData = FloatArray(size)

        var kernelSize = (6 * sigma).toInt()
        if (kernelSize % 2 == 0) {
            kernelSize++ // 确保核的大小为奇数
        }
        val kernel = generateGaussianKernel(sigma, kernelSize)

        val radius = kernelSize / 2

        for (i in 0 until size) {
            var smoothedValue = 0.0f

            for (j in -radius..radius) {
                val index = i + j
                if (index in 0 until size) {
                    smoothedValue += (ttf[index] * kernel[j + radius]).toFloat()
                }
            }

            smoothedData[i] = smoothedValue
        }

        return smoothedData
    }
    private fun generateGaussianKernel(sigma: Float, size: Int): DoubleArray {
        val kernel = DoubleArray(size)
        var sum = 0.0
        val radius = size / 2
        for (x in -radius..radius) {
            val exponent = -((x * x).toDouble() / (2.0 * sigma * sigma))
            kernel[x + radius] = exp(exponent)
            sum += kernel[x + radius]
        }
        // 标准化核
        for (x in 0 until size) {
            kernel[x] /= sum
        }
        return kernel
    }


    /**
     * 指数平滑算法
     * alpha 0.1 - 2
     */
    fun applySmoothExponential(ttf: FloatArray?, alpha: Float): FloatArray? {
        if (ttf == null || ttf.isEmpty()) return ttf
        val smoothedData = FloatArray(ttf.size)
        var lastSmoothedValue: Float? = null
        for (i in ttf.indices) {
            val dataPoint = ttf[i]
            val smoothedValue = if (lastSmoothedValue == null) {
                // 初始情况：第一个数据点作为初始平滑值
                dataPoint
            } else {
                alpha * dataPoint + (1 - alpha) * lastSmoothedValue
            }
            smoothedData[i] = smoothedValue
            lastSmoothedValue = smoothedValue
        }
        return smoothedData
    }



















    /**
     * 数据 阻力
     * 值越大阻力越大
     *
     * @param `data` 数据
     * @param var  值
     * @return  FloatArray
     */
    fun dataResistance(`data`: FloatArray?, `var`: Float): FloatArray? {
        if (`data` == null || `data`.isEmpty() || `var` == 0f) return `data`
        var max = -1f
        for (datum in `data`) if (datum > max) max = datum
        val v = max * 0.2f
        for (i in `data`.indices) {
            if (`data`[i] == 0f) continue
            if (`data`[i] < v) continue
            `data`[i] = max / `data`[i] * `var` + `data`[i]
        }
        return `data`
    }


    /**
     * 数据放大
     *
     * @param data 数据
     * @param var  值
     * @return FloatArray
     */
    fun dataTransform(`data`: FloatArray?, `var`: Float, maxVar: Float): FloatArray? {
        if (`data` == null || `data`.isEmpty()) return `data`
        for (i in `data`.indices) {
            var v = `data`[i] * `var`
            if (v > maxVar) v = maxVar
            `data`[i] = v
        }
        return `data`
    }


    /**
     * 数据填充
     * 如果长则剪裁
     * 如果短则补0
     *
     * @param data   源数据
     * @param length 目标长度
     * @return FloatArray
     */
    fun dataFilling(`data`: FloatArray?, length: Int): FloatArray? {
        if (`data` == null || `data`.isEmpty() || length <= 0 || `data`.size == length) return `data`

        val l1 = FloatArray(length)
        if (`data`.size < length) {
            System.arraycopy(`data`, 0, l1, 0, `data`.size)
            return l1
        }
        System.arraycopy(`data`, 0, l1, 0, length)
        return l1
    }


    /**
     * 数据反向合并
     *
     * @param data 源数据
     * @return FloatArray
     */
    fun symmetry(`data`: FloatArray?): FloatArray? {
        if (`data` == null || `data`.isEmpty()) return `data`
        val fst = FloatArray(`data`.size * 2)
        run {
            var i = `data`.size
            var j = 0
            while (i < fst.size) {
                fst[i] = `data`[j]
                i++
                j++
            }
        }
        var i = `data`.size - 1
        var j = 0
        while (i >= 0) {
            fst[j] = `data`[i]
            i--
            j++
        }
        return fst
    }
    
}