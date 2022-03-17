package com.pikachu.visualization.audio;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * @author pkpk.run
 * @project audio
 * @package com.pikachu.visualization.audio
 * @date 2022/03/17
 * @description 音频可视化数据处理  继承此类
 */
public abstract class BaseAudioView extends View {



    // 是否镜像
    private boolean isMirror = true;
    // 跳过开头的第几个值  靠前值浮动大
    private int countIndex = 0;
    // 是否平滑
    private boolean isSmooth = true;
    // 平滑区间
    private int smoothInterval = 3;
    // 动画速度 ms    采样时间也同这个速度
    private int animationSpeed = 100;
    // 差值器
    private TimeInterpolator timeInterpolator = new DecelerateInterpolator();
    // 最大值 值
    private float maxRange = 200;
    // 幅度
    private float range = 5;
    // 阻力 值越大加的越少
    private float resistance = 0;


    private boolean isRunView = true;
    private boolean isRun = true;
    private float[] upData = {};
    private float[] transform = {};
    private final AudioUtils instance;
    public AnimatorSet animatorSet;


    public BaseAudioView(Context context) {
        this(context, null);
    }

    public BaseAudioView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        instance = AudioUtils.getInstance();
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = onAudioMeasure(widthMeasureSpec, heightMeasureSpec);
        //count = count + (isMirror ? countIndex / 2  : countIndex);
        //LogsUtils.showLog_Mg(countIndex);
        count = Math.min(count, 1023); // 最大1024
        instance.setCount(isMirror ? count / 2  + 1: count);
    }


    /**
     * @param widthMeasureSpec  宽度测量规范
     * @param heightMeasureSpec 高度测量规范
     * @return 返回音频个数
     */
    protected abstract int onAudioMeasure(int widthMeasureSpec, int heightMeasureSpec);


    /**
     * 画数据时
     * 继承后直接在这里面画即可  处理后的FFT数据 transform
     *
     * @param canvas    画布
     * @param transform 值
     */
    protected abstract void drawView(Canvas canvas, float[] transform);


    /**
     * 在一个动画执行时
     *
     * @param canvas 画布
     * @param model  当前数据
     * @param upData 上次数据
     */
    protected void onAnimatorStart(Canvas canvas, float[] model, float[] upData) {

    }


    public int[] reMeasure(int widthMeasureSpec,
                           int heightMeasureSpec,
                           int default_width,
                           int default_height) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (measureWidthMode == MeasureSpec.AT_MOST
                && measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(default_width, default_height);
        } else if (measureWidthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(default_width, measureHeight);
        } else if (measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(measureWidth, default_height);
        } else {
            setMeasuredDimension(measureWidth, measureHeight);
        }
        return new int[]{getMeasuredWidth(), getMeasuredHeight()};
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        if (isRun) {
            float[] model = dataFilling(instance.getModel(), instance.getCount());
            model = dataResistance(model, resistance);
            model = dataTransform(model, range, maxRange);

            if (isMirror)
                model = symmetry(model);
            if (isSmooth){
                model = smoothNum(model, smoothInterval);
                model = smoothNum(model);
            }

            onAnimatorStart(canvas, model, upData);
            startAnimator(upData, model);
            upData = model;
        }
        drawView(canvas, transform);
        if (!isRunView) return;
        postInvalidate(); // 压榨性能 =_=
    }


    private void startAnimator(float[] to, float[] model) {
        if (to == null || to.length <= 0 || model == null || model.length <= 0) {
            return;
        }

        if (to.length != model.length) {
            to = dataFilling(to, model.length);
        }

        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
            animatorSet = null;
        }

        float[] dd = new float[model.length];
        animatorSet = new AnimatorSet();
        ValueAnimator[] valueAnimators = new ValueAnimator[model.length];
        for (int i = 0; i < model.length; i++) {
            int iFind = i;
            @SuppressLint("Recycle")
            ValueAnimator mLightWaveAnimator = ValueAnimator.ofFloat(to[i], model[i]);
            mLightWaveAnimator.addUpdateListener(animation -> {
                dd[iFind] = (float) animation.getAnimatedValue();
                transform = dd;
                //postInvalidate();
            });
            valueAnimators[i] = mLightWaveAnimator;
        }
        animatorSet.playTogether(valueAnimators);
        animatorSet.setDuration(animationSpeed);
        animatorSet.setInterpolator(timeInterpolator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isRun = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRun = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    /**
     * 数据平滑
     * 这里直接用 平均法
     * Savitzky-Golay 算法 效果更好
     *
     * @param data     源数据
     * @param interval 窗口
     * @return float[]
     */
    public static float[] smoothNum(float[] data, int interval) {
        if (data == null || data.length <= 0 || interval <= 0)
            return data;
        float[] fst = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            float var = 0;
            if (i + interval < data.length) {
                for (int j = 0; j < interval; j++) {
                    var += data[i + j];
                }
                fst[i] = var / interval;
            } else {
                int i1 = data.length - i;
                for (int j = 0; j < i1; j++) {
                    var += data[i + j];
                }
                fst[i] = var / i1;
            }
        }
        return fst;
    }


    /**
     * 数据平滑
     *
     * @param data     源数据
     * @return float[]
     */
    public static float[] smoothNum(float[] data) {
        if (data == null || data.length <= 0 )
            return data;

        int ft;
        int length = data.length;
        float[] ftAr1 = new float[(length + 14)];
        for (int i3 = 0; i3 < ftAr1.length; i3++) {
            if (i3 < 7) {
                ftAr1[i3] = data[(length - 7) + i3];
            } else if (i3 >= ftAr1.length - 7) {
                ftAr1[i3] = data[(i3 - length) - 7];
            } else {
                ftAr1[i3] = data[i3 - 7];
            }
        }
        int length2 = ftAr1.length;
        float[] fArr4 = new float[length2];
        if (length2 < 7) {
            if (length2 - 2 >= 0)
                System.arraycopy(ftAr1, 0, fArr4, 0, length2 - 2);
        } else {
            int i5 = 3;
            fArr4[0] = (((((((ftAr1[0] * 39.0f) + (ftAr1[1] * 8.0f))
                    - (ftAr1[2] * 4.0f)) - (ftAr1[3] * 4.0f))
                    + (ftAr1[4])) + (ftAr1[5] * 4.0f))
                    - (ftAr1[6] * 2.0f)) / 42.0f;
            fArr4[1] = (((((((ftAr1[0] * 8.0f) + (ftAr1[1] * 19.0f))
                    + (ftAr1[2] * 16.0f)) + (ftAr1[3] * 6.0f))
                    - (ftAr1[4] * 4.0f)) - (ftAr1[5] * 7.0f))
                    + (ftAr1[6] * 4.0f)) / 42.0f;
            fArr4[2] = (((((((ftAr1[0] * -4.0f) + (ftAr1[1] * 16.0f))
                    + (ftAr1[2] * 19.0f)) + (ftAr1[3] * 12.0f))
                    + (ftAr1[4] * 2.0f)) - (ftAr1[5] * 4.0f))
                    + (ftAr1[6])) / 42.0f;
            while (true) {
                ft = length2 - 4;
                if (i5 > ft) {
                    break;
                }
                int i6 = i5 + 1;
                fArr4[i5] = (((((ftAr1[i5 - 3] + ftAr1[i5 + 3]) * -2.0f)
                        + ((ftAr1[i5 - 2] + ftAr1[i5 + 2]) * 3.0f))
                        + ((ftAr1[i5 - 1] + ftAr1[i6]) * 6.0f))
                        + (ftAr1[i5] * 7.0f)) / 21.0f;
                i5 = i6;
            }
            int fp7 = length2 - 3;
            int fp8 = length2 - 1;
            int fp9 = length2 - 2;
            int fp10 = length2 - 5;
            int fp11 = length2 - 6;
            int fp12 = length2 - 7;
            fArr4[fp7] = (((((((ftAr1[fp8] * -4.0f) + (ftAr1[fp9] * 16.0f))
                    + (ftAr1[fp7] * 19.0f)) + (ftAr1[ft] * 12.0f))
                    + (ftAr1[fp10] * 2.0f)) - (ftAr1[fp11] * 4.0f))
                    + (ftAr1[fp12])) / 42.0f;
            fArr4[fp9] = (((((((ftAr1[fp8] * 8.0f) + (ftAr1[fp9] * 19.0f))
                    + (ftAr1[fp7] * 16.0f)) + (ftAr1[ft] * 6.0f))
                    - (ftAr1[fp10] * 4.0f)) - (ftAr1[fp11] * 7.0f))
                    + (ftAr1[fp12] * 4.0f)) / 42.0f;
            fArr4[fp8] = (((((((ftAr1[fp8] * 39.0f) + (ftAr1[fp9] * 8.0f))
                    - (ftAr1[fp7] * 4.0f)) - (ftAr1[ft] * 4.0f))
                    + (ftAr1[fp10])) + (ftAr1[fp11] * 4.0f))
                    - (ftAr1[fp12] * 2.0f)) / 42.0f;
        }
        float[] fArr5 = new float[data.length];
        System.arraycopy(fArr4, 7, fArr5, 0, data.length);
        return fArr5;
    }





    /**
     * 数据反向合并
     *
     * @param data 源数据
     * @return float[]
     */
    public static float[] symmetry(float[] data) {
        if (data == null || data.length <= 0)
            return data;
        float[] fst = new float[data.length * 2];

        for (int i = data.length, j = 0; i < fst.length; i++, j++) {
            fst[i] = data[j];
        }
        for (int i = data.length - 1, j = 0; i >= 0; i--, j++) {
            fst[j] = data[i];
        }
        return fst;
    }


    /**
     * 数据填充
     * 如果长则剪裁
     * 如果短则补0
     *
     * @param data   源数据
     * @param length 目标长度
     * @return float[]
     */
    public static float[] dataFilling(float[] data, int length) {
        if (data == null || data.length <= 0 || length <= 0 || data.length == length) {
            return data;
        }
        float[] l1 = new float[length];
        if (data.length < length) {
            System.arraycopy(data, 0, l1, 0, data.length);
            return l1;
        }
        System.arraycopy(data, 0, l1, 0, length);
        return l1;
    }


    /**
     * 数据放大
     *
     * @param data 数据
     * @param var  值
     * @return float[]
     */
    public static float[] dataTransform(float[] data, float var, float maxVar) {
        if (data == null || data.length <= 0) {
            return data;
        }
        for (int i = 0; i < data.length; i++) {
            float v = data[i] * var;
            if (v > maxVar) v = maxVar;
            data[i] = v;
        }
        return data;
    }



    /**
     * 数据 阻力
     * 值越大阻力越大
     *
     * @param data 数据
     * @param var  值
     * @return float[]
     */
    public static float[] dataResistance(float[] data, float var) {
        if (data == null || data.length <= 0 || var == 0) {
            return data;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) continue;
            data[i] = (1 / data[i] * var) + data[i];
        }
        return data;
    }


    // 开始
    public void start() {
        if (instance != null) {
            instance.start();
            instance.setCountIndex(countIndex);
        }
        isRunView = true;
        postInvalidate();
    }

    // 结束
    public void stop() {
        if (instance != null) {
            instance.stop();
        }
        isRunView = false;
        postInvalidate();
    }


    // 是否镜像
    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    // 跳过开头的第几个值  靠前值浮动大
    public void setCountIndex(int countIndex) {
        this.countIndex = countIndex;
    }

    // 是否平滑
    public void setSmooth(boolean smooth) {
        isSmooth = smooth;
    }

    // 平滑区间
    public void setSmoothInterval(int smoothInterval) {
        this.smoothInterval = smoothInterval;
    }

    // 动画速度 ms
    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    // 差值器
    public void setTimeInterpolator(TimeInterpolator timeInterpolator) {
        this.timeInterpolator = timeInterpolator;
    }


    // 最大值
    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    // 幅度
    public void setRange(float range) {
        this.range = range;
    }

    // 阻力 值越大加的越少
    public void setResistance(float resistance) {
        this.resistance = resistance;
    }





    /**
     * 计算圆上点坐标
     *
     * @param centerX 圆心 x
     * @param centerY 圆心 y
     * @param radius  半径
     * @param angle   角度
     * @return { [0] => x , [1] => y }
     */
    public static float[] computeCircleXY(float centerX, float centerY, float radius, float angle) {
        double v4 = (angle - 90) * Math.PI / 180;
        float m1X = centerX + radius * (float) Math.cos(v4);
        float m1Y = centerY + radius * (float) Math.sin(v4);
        return new float[]{m1X, m1Y};
    }
}
