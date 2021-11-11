package com.extend.englishShots.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import com.extend.englishShots.R
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class PhoneUnlockedReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG","RECEIVED SCREEN UNLOCK!")

            val mParams: WindowManager.LayoutParams? = WindowManager.LayoutParams(
                200,
                400,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT)

            val testView = LayoutInflater.from(context).inflate(R.layout.fish_layout, null)
            val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.addView(testView, mParams)

            CoroutineScope(Dispatchers.IO).launch {
                delay(TimeUnit.SECONDS.toMillis(3))
                withContext(Dispatchers.Main) {
                    Log.i("TAG", "this will be called after 3 seconds")
                    wm.removeView(testView);
                }
            }
        }
}