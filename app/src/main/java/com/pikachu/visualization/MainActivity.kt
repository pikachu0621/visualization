package com.pikachu.visualization;

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.pikachu.visualization.adapter.AnnularAdapter
import com.pikachu.visualization.adapter.AnnularAdapter1
import com.pikachu.visualization.adapter.StraightSideAdapter
import com.pikachu.visualization.audio.AudioVisualizerController
import com.pikachu.visualization.audio.AudioVisualizerController.Companion.SYS_AUDIO_SESSION
import com.pikachu.visualization.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setConfig()
        list()

    }

    private fun setConfig() {
        binding.sysAudio.setOnClickListener {
            mediaPlayer ?. release()
            AudioVisualizerController.setAudioSession(SYS_AUDIO_SESSION)
            Toast.makeText(this, "设置完成", Toast.LENGTH_SHORT).show()
        }
        binding.appAudio.setOnClickListener {
            if (mediaPlayer != null) mediaPlayer!!.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.dia)
            mediaPlayer!!.isLooping = true
            AudioVisualizerController.setAudioSession(mediaPlayer!!.audioSessionId)
            Toast.makeText(this, "设置完成，即将播放音乐", Toast.LENGTH_SHORT).show()
            mediaPlayer!!.start()

        }
    }


    private fun list() {
        binding.audioType.apply {
            adapter = AudioTypeViewAdapter(
                listOf(
                    AudioTypeViewAdapter.AudioTypeData("环形效果1", AnnularAdapter::class.java),
                    AudioTypeViewAdapter.AudioTypeData("环形效果2", AnnularAdapter1::class.java),
                    AudioTypeViewAdapter.AudioTypeData("直方样式1", StraightSideAdapter::class.java),
                )
            ) {
                startPermission(this@MainActivity, object : PermissionCallback {
                    override fun onGranted() {
                        PlayActivity.startPlayActivity(this@MainActivity, it.clazz)
                    }
                })
            }
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }


    interface PermissionCallback {
        fun onGranted()
        fun onDenied() {

        }
    }

    companion object {
        fun startPermission(context: Context, permissionCallback: PermissionCallback) {
            XXPermissions.with(context)
                .permission(Permission.RECORD_AUDIO)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all) {
                            permissionCallback.onGranted()
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            toast(context, R.string.record_pm)
                            XXPermissions.startPermissionActivity(context, permissions)
                        } else {
                            toast(context, R.string.record_pms)
                        }
                        permissionCallback.onDenied()
                    }
                })
        }

        fun toast(context: Context, @StringRes strInt: Int) {
            Toast.makeText(context, strInt, Toast.LENGTH_SHORT).show()
        }
    }


}