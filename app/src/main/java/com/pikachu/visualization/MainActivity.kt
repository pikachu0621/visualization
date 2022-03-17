package com.pikachu.visualization;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.HalfFloat
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.pikachu.visualization.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    private lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.appCompatButton.setOnClickListener {
            startActivity(Intent(this, AnnularActivity::class.java))
        }
        binding.appCompatButton1.setOnClickListener{
            startActivity(Intent(this, StraightSideActivity::class.java))
        }
    }




    interface PermissionCallback{
       fun onGranted()
       fun onDenied(){

       }
    }

    companion object{
        fun startPermission(context: Context, permissionCallback : PermissionCallback){
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
        fun toast(context:Context, @StringRes strInt : Int){
            Toast.makeText(context, strInt, Toast.LENGTH_SHORT).show()
        }
    }


}
