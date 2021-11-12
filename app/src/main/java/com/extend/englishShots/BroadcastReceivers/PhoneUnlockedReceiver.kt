package com.extend.englishShots.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.TextView
import com.extend.englishShots.R
import com.extend.englishShots.Utils.FileManager
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PhoneUnlockedReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG","RECEIVED SCREEN UNLOCK!")

        val mParams: WindowManager.LayoutParams? = WindowManager.LayoutParams(
            MATCH_PARENT,
            400,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT)
        val fileManager = FileManager()
        val dictionaryMap = fileManager.getObjectFromString(context)
        if (mParams != null) {
            mParams.x = 10
        };
        val keysAsArray = ArrayList<String>(dictionaryMap.keys)
        val r = Random()
        val randomKey = r.nextInt(keysAsArray.size)
        val key = keysAsArray.get(randomKey);
        val translation = dictionaryMap.get(key)
        val testView = LayoutInflater.from(context).inflate(R.layout.fish_layout, null)
        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        testView.findViewById<TextView>(R.id.msg).setText(key)
        wm.addView(testView, mParams)


        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(2))
            withContext(Dispatchers.Main) {
                Log.i("TAG", "this will be called after 3 seconds")
                testView.findViewById<TextView>(R.id.msg).setText(translation)

            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(4))
            withContext(Dispatchers.Main) {
                Log.i("TAG", "this will be called after 3 seconds")
                wm.removeView(testView);
            }
        }
    }
}