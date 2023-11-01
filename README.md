音频可视化
===
提供比较流畅的音频可视化组件，同时可以自定义频谱图


环形样式 AnnularAdapter
----
![](assets/au.png)


直方样式 StraightSideAdapter
----
![](assets/se.png)

演示
---
[安装APP](assets/release/app-release.apk)
<br>




用法
---
>1. 权限
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```
<br>


>2. 布局引用
```xml
    <com.pikachu.visualization.audio.VisualizationAudioView
        android:id="@+id/audio"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
<br>

>3. 控制  当然还要播放一首音乐
```kotlin
// 设置适配器 , 绑定生命周期  (目前只画了 AnnularAdapter, StraightSideAdapter 两个样式)
audio.setAdapter(VisualizationAudioAdapter(), this)
// 启动  必须有权限(动态申请)
audio.start()
// 停止
audio.stop()
```
<br>










自定义
---
>1. 继承 VisualizationAudioAdapter 适配器， 实现以下方法
```kotlin

/**
 *
 * 测量View 数据
 *
 * @return 需要多少条数据源
 */
abstract fun onAudioViewMeasure(measureWidth: Int, measureHeight: Int, view: VisualizationAudioView): Int

/**
 * 画布
 *
 * @param canvas    画布
 * @param transform 处理后的FFT数据
 */
abstract fun drawView(canvas: Canvas, transform: FloatArray, view: VisualizationAudioView)

```
<br>


>2. 一些属性设置
```kotlin
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

```
