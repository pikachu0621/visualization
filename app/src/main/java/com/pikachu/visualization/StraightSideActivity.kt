package com.pikachu.visualization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.pikachu.visualization.databinding.ActivityStraightSideBinding

class StraightSideActivity : AppCompatActivity() {

    private lateinit var binding:ActivityStraightSideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStraightSideBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        MainActivity.startPermission(this, object : MainActivity.PermissionCallback {
            override fun onGranted() {
                binding.audio.start()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.audio.stop()
    }
}