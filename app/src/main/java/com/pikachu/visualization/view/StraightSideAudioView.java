package com.pikachu.visualization.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.pikachu.visualization.audio.BaseAudioView;

/**
 * @author pkpk.run
 * @project audio
 * @package com.pikachu.visualization.view
 * @date 2022/03/17
 * @description 直方频谱图
 */
public class StraightSideAudioView extends BaseAudioView {

    private final static int DEFAULT_WIDTH = 400, DEFAULT_HEIGHT = 200;

    private int linWidth = 40; // 宽
    private int clearance = 2; // 间隙
    private int minHeight = 10; // 没有音乐时高度



    Paint mPaint = new Paint();
    private int t1;
    public Path path;

    public StraightSideAudioView(Context context) {
        this(context, null);
    }

    public StraightSideAudioView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StraightSideAudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        path = new Path();


        setMirror(true); // 是否镜像
        setSmooth(true); // 是否平滑
        setSmoothInterval(3); // 平滑窗口
        setCountIndex(0);  // 跳过开头的第几个值
        setAnimationSpeed(80); // 动画速度
        setTimeInterpolator(new LinearInterpolator()); // 动画差值器
        setRange(7); // 幅度
    }

    @Override
    protected int onAudioMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] ints = reMeasure(widthMeasureSpec, heightMeasureSpec, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        int width = ints[0];
        int height = ints[1];
        t1 = linWidth + clearance;
        int count = width / t1;
        setMaxRange(height);
        return count / 3;
    }

    @Override
    protected void drawView(Canvas canvas, float[] transform) {
        // 根据值直接画即可


/*        path.reset();
        mPaint.setStyle(Paint.Style.FILL);
        path.moveTo(0,getHeight());*/

     /*   float x = 0;
        float y = getHeight();
*/
        for (int i = 0, j = 0; i < transform.length; i++, j += 3) {
     /*       if (i % 4 != 0){
                continue;
            }
*/


            float v = transform[i];
            if (v <= minHeight ){
                v = getHeight() - minHeight;
            }else {
                v = getHeight() - v;
            }

            int tt = t1 * j;
            Shader mShader = new LinearGradient(
                    tt + (linWidth >> 1),
                    getHeight(),
                    tt + (linWidth >> 1), v,
                    new int[]{0xFF98FB98, Color.TRANSPARENT, 0xFFFF69B4/*, Color.TRANSPARENT*/},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(mShader);

            canvas.drawRect(tt, v,
                    tt + linWidth, getHeight(),
                    mPaint
            );


/*            if (i % 5 == 0){
                mPaint.setColor(Color.WHITE);
                path.quadTo(x,  y, tt + (linWidth >> 1), v);
                x =  tt + (linWidth >> 1);
                y = v;
                canvas.drawCircle(tt + (linWidth >> 1), v - 10, 2, mPaint);
            }*/

        }
/*        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(1);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(path, mPaint);*/
    }

}
