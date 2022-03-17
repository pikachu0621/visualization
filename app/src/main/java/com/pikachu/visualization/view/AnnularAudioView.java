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
 * @description 环形频谱图
 */
public class AnnularAudioView extends BaseAudioView {

    private final static int DEFAULT_WIDTH = 400, DEFAULT_HEIGHT = 400;

    private int linWidth = 4; // 宽
    private int minHeight = 5; // 没有音乐时高度
    private int size = 100; // 大小


    Paint mPaint = new Paint();
    public Path path;
    public int count = 180;
    public int width;
    public int height;
    public int roundDia;
    public float roundX;
    public float roundY;
    public float radius;
    public float minRadius;

    public AnnularAudioView(Context context) {
        this(context, null);
    }

    public AnnularAudioView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnularAudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(linWidth);
        mPaint.setStyle(Paint.Style.STROKE);


        setMirror(true); // 是否镜像
        setSmooth(true); // 是否平滑
        setSmoothInterval(6); // 平滑窗口
        setCountIndex(0);  // 跳过开头的第几个值
        setAnimationSpeed(100); // 动画速度
        setTimeInterpolator(new LinearInterpolator()); // 动画差值器
        setRange(7); // 幅度
        //setResistance(1.2F); // 阻力


    }

    @Override
    protected int onAudioMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] ints = reMeasure(widthMeasureSpec, heightMeasureSpec, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        width = ints[0];
        height = ints[1];
        roundDia = Math.min(width, height);
        radius = roundDia / 2F;
        roundX = width / 2F;
        roundY = height / 2F;
        minRadius = radius / 2 + size;
        setMaxRange(radius);
        return count;
    }

    @Override
    protected void drawView(Canvas canvas, float[] transform) {


        //drawLines
   /*     float[] xy = new float[count * 4];*/
/*        Path path = new Path();
        Path path1 = new Path();*/



        for (int angle = 0, an = 0; angle < transform.length; angle++, an += 2) {


           /* if (angle % 3 != 0)
                continue;*/

            float var = transform[angle];
            float var2 = var / 2;

            if (var2 <= minHeight) {
                var2 = minHeight;
            }

            float[] round1 = BaseAudioView.computeCircleXY(roundX, roundY, minRadius, 180 - an);
            float[] round2 = BaseAudioView.computeCircleXY(round1[0], round1[1], var2, -an);
            float[] round3 = BaseAudioView.computeCircleXY(round1[0], round1[1], var2, 180 - an);

            //drawLines

/*          int angleXy = angle * 4;
            xy[angleXy] = round2[0]; // startX
            xy[angleXy + 1] = round2[1]; // startY
            xy[angleXy + 2] = round3[0]; // endX
            xy[angleXy + 3] = round3[1]; // endY*/


/*            if (angle == 0) {
                path.moveTo(round3[0], round3[1]);
                path1.moveTo(round2[0], round2[1]);
            } else{
                path.lineTo(round3[0], round3[1]);
                path1.lineTo(round2[0], round2[1]);
            }*/




            Shader mShader = new LinearGradient(
                    round2[0],
                    round2[1],
                    round3[0],
                    round3[1],
                    new int[]{0xFFFF69B4, Color.TRANSPARENT, 0xFF98FB98},
                    null, Shader.TileMode.REPEAT);
            mPaint.setShader(mShader);

            canvas.drawLine(
                    round2[0],
                    round2[1],
                    round3[0],
                    round3[1],
                    mPaint);

        }



/*        mPaint.setShader(null);
        mPaint.setColor(0xFF98FB98);
        canvas.drawCircle(roundX, roundY, minRadius, mPaint);*/

/*        path.close();
        path1.close();
        canvas.drawPath(path, mPaint);
        canvas.drawPath(path1, mPaint);*/

        // 用 drawLines 会快点 但是无法渐变
        /*canvas.drawLines(xy, mPaint);*/
    }

}
