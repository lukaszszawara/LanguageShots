package com.extend.englishShots.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.TextView
import com.extend.englishShots.R
import com.extend.englishShots.Utils.DraggableTouchListener
import com.extend.englishShots.Utils.FileManager
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PhoneUnlockedReceiver : BroadcastReceiver() {

    var mParams: WindowManager.LayoutParams? = null;
    var wm : WindowManager? = null;
    var rootView : View? = null;
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG","RECEIVED SCREEN UNLOCK!")

        mParams = WindowManager.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT)
        val fileManager = FileManager()
        val dictionaryMap = fileManager.getObjectFromString(context)

        val keysAsArray = ArrayList<String>(dictionaryMap.keys)
        val r = Random()
        val randomKey = r.nextInt(keysAsArray.size)
        val key = keysAsArray.get(randomKey);
        val translation = dictionaryMap.get(key)
        rootView = LayoutInflater.from(context).inflate(R.layout.fish_layout, null)
        wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        rootView!!.findViewById<TextView>(R.id.msg).setText(key)
        wm!!.addView(rootView, mParams)
        if (mParams != null) {
            setPosition(200,200)
        };
        rootView!!.findViewById<View>(R.id.msg).registerDraggableTouchListener(
            initialPosition = { Point(mParams!!.x, mParams!!.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(4))
            withContext(Dispatchers.Main) {
                Log.i("TAG", "this will be called after 3 seconds")
                rootView!!.findViewById<TextView>(R.id.msg).setText(translation)

            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(8))
            withContext(Dispatchers.Main) {
                Log.i("TAG", "this will be called after 3 seconds")
                wm!!.removeView(rootView);
            }
        }
    }

    private fun setPosition(x: Int, y: Int) {
        if(mParams != null) {
            mParams!!.x = x
            mParams!!.y = y
            update()
        }
    }

    private fun update() {
        try {
            wm!!.updateViewLayout(rootView, mParams)
        } catch (e: Exception) {
            Log.e("TAG",e.localizedMessage);
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    fun View.registerDraggableTouchListener(
        initialPosition: () -> Point,
        positionListener: (x: Int, y: Int) -> Unit
    ) {
        DraggableTouchListener(context, this, initialPosition, positionListener)
    }
}