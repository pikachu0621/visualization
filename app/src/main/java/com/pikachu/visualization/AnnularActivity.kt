package com.pikachu.visualization

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.pikachu.visualization.MainActivity.PermissionCallback
import com.pikachu.visualization.databinding.ActivityAnnularBinding
import com.pikachu.visualization.databinding.ActivityStraightSideBinding

class AnnularActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnnularBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnularBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        MainActivity.startPermission(this, object : PermissionCallback{
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