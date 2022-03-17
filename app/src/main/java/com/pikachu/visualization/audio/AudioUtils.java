package com.pikachu.visualization.audio;

import android.media.audiofx.Visualizer;


/**
 * @author pkpk.run
 * @project audio
 * @package com.pikachu.visualization.audio
 * @date 2022/03/17
 * @description 获取系统音频
 */
public final class AudioUtils {

    private Visualizer visualizer;
    private static AudioUtils audioUtils;
    private FftListener fftListener;
    private int count = 0;
    private int countIndex = 1; // 跳过开头的第几个值
    public float[] model;


    public interface FftListener {
        // 回调
        void onFftData(float[] model);
    }


    //@RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    public static AudioUtils getInstance() {
        if (audioUtils == null) {
            synchronized (AudioUtils.class) {
                if (audioUtils == null) {
                    audioUtils = new AudioUtils();
                }
            }
        }
        return audioUtils;
    }


    // 设置监听
    public void setFftListener(FftListener fftListener) {
        this.fftListener = fftListener;
    }

    // 设置长度
    public void setCount(int count) {
        this.count = count;
    }

    // 获取当前一组数据
    public float[] getModel() {
        if (count <= 0 || model == null || model.length < count + countIndex)
            return model;
        float[] floats = new float[count];
        System.arraycopy(model, countIndex, floats, 0, floats.length);
        return floats;
    }


    // 跳过开头的第几个值
    public void setCountIndex(int countIndex) {
        this.countIndex = Math.max(countIndex, 0);
    }


    public int getCount() {
        return count;
    }


    private AudioUtils() {

    }

    private void init() {
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
            visualizer = null;
            //System.gc();
        }
        visualizer = new Visualizer(0);
        visualizer.setEnabled(false);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                int f = fft.length / 2;
                model = new float[f + 1];
                model[0] = (float) Math.abs(fft[1]);
                int j = 1;
                if (count <= 0 || count > f ) {
                    count = f;
                }
                for (int i = 2; i < count * 2; ) {
                    model[j] = (float) Math.hypot(fft[i], fft[i + 1]);
                    i += 2;
                    j++;
                    model[j] = Math.abs(model[j]);
                }
                if (fftListener != null) {
                    fftListener.onFftData(model);
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);
        visualizer.setEnabled(true);
    }


    public void stop() {
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
        audioUtils = null;
    }


    public int getCountIndex() {
        return countIndex;
    }

    public void start() {
        init();
    }













}
