package com.pikachu.visualization

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.pikachu.visualization.audio.VisualizationAudioAdapter
import com.pikachu.visualization.databinding.ActivityPalyBinding

private const val START_KEY = "start_key"

class PlayActivity : AppCompatActivity() {

    private var adapterClassName: String? = null
    private lateinit var binding :ActivityPalyBinding
    companion object {
        fun startPlayActivity(
            activity: Activity,
            adapterClazz: Class<out VisualizationAudioAdapter>
        ) {
            activity.startActivity(Intent(activity, PlayActivity::class.java).apply {
                putExtra(START_KEY, adapterClazz.canonicalName)
            })
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPalyBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        adapterClassName = intent.getStringExtra(START_KEY)
        adapterClassName ?: let {
            finish()
            Toast.makeText(this, "适配器错误1", Toast.LENGTH_SHORT).show()
            return
        }
        val createAdapter = createAdapter() ?: let {
            finish()
            Toast.makeText(this, "适配器错误2", Toast.LENGTH_SHORT).show()
            return
        }
        binding.annularAudio.setAdapter(createAdapter, this)
        binding.annularAudio.start()

        binding.landscape.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        binding.portraitScreen.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    }


    private fun createAdapter(): VisualizationAudioAdapter? {
        return try {
            val clazz = Class.forName(adapterClassName!!)
            clazz.getDeclaredConstructor().newInstance() as VisualizationAudioAdapter
        } catch (e: Exception) {
            null
        }
    }

    private fun sysUiFlag(){
        ImmersionBar.with(this)
            .statusBarDarkFont(false) //  true 深色
            .fitsSystemWindows(false)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
    }

    override fun onResume() {
        super.onResume()
        sysUiFlag()
    }


}