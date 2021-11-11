package com.extend.englishShots

import android.app.ActivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.extend.englishShots.Services.ForegroundService


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!Settings.canDrawOverlays(this)) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
        }


        if(isMyServiceRunning() == false) {
            ForegroundService.startService(this, "Foreground Service is running...")
            Log.i("TAG", "this will be called immediately")
        }else{
            ForegroundService.stopService(this)
            ForegroundService.startService(this, "Foreground Service is running...")

        }
    }


    fun isMyServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (ForegroundService::class.simpleName == service.service.className) {
                return true
            }
        }
        return false
    }
    }
