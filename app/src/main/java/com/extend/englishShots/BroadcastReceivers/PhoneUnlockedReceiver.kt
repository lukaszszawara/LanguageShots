package com.extend.englishShots.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.graphics.Point
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.TextView
import com.extend.englishShots.R
import com.extend.englishShots.Utils.Const
import com.extend.englishShots.Utils.DraggableTouchListener
import com.extend.englishShots.Utils.FileManager
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PhoneUnlockedReceiver : BroadcastReceiver() {

    private var mParams: WindowManager.LayoutParams? = null
    private var wm : WindowManager? = null
    private var rootView : View? = null
    private var context : Context? = null
    private var sharedPreferences : SharedPreferences? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG","RECEIVED SCREEN UNLOCK!")
        this.context = context
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
        val key = keysAsArray[randomKey]
        val translation = dictionaryMap[key]
        context?.getSharedPreferences(Const.PREF_KEY,Context.MODE_PRIVATE)
            .also { this.sharedPreferences = it }
        var temporaryView = LayoutInflater.from(context).inflate(R.layout.fish_layout, null)
        (context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).also { wm = it }
        key.also { temporaryView!!.findViewById<TextView>(R.id.msg).text = it }
        if(this.rootView == null) {
            wm!!.addView(temporaryView, mParams)
            rootView = temporaryView;
        }
        if (mParams != null) {
            sharedPreferences?.let { setPosition(it.getInt(Const.X_KEY,200),it.getInt(Const.Y_KEY,200)) }
        }
        rootView!!.findViewById<View>(R.id.msg).registerDraggableTouchListener(
            initialPosition = { Point(mParams!!.x, mParams!!.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )


        rootView!!.findViewById<View>(R.id.frame).registerDraggableTouchListener(
            initialPosition = { Point(mParams!!.x, mParams!!.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )

        rootView!!.findViewById<View>(R.id.close_button).setOnClickListener {
            wm!!.removeView(rootView)
            rootView = null;
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(Const.TIMEOUT_TRANSITION))
            withContext(Dispatchers.Main) {
                if(rootView != null) {
                    rootView!!.findViewById<TextView>(R.id.msg).text = translation
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(Const.TIMEOUT_DISSAPEAR))
            withContext(Dispatchers.Main) {
                Log.i("TAG", "this will be called after 3 seconds")
                if(rootView != null) {
                    wm!!.removeView(rootView)
                    rootView = null
                }
            }
        }
    }

    private fun setPosition(x: Int, y: Int) {
        if(mParams != null) {
            mParams!!.x = x
            mParams!!.y = y
            if (sharedPreferences != null) {
                sharedPreferences!!.edit().putInt(Const.X_KEY,x).apply()
                sharedPreferences!!.edit().putInt(Const.Y_KEY,y).apply()
            }
            update()
        }
    }

    private fun update() {
        try {
            wm!!.updateViewLayout(rootView, mParams)
        } catch (e: Exception) {
            Log.e("TAG", e.message.toString())
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    private fun View.registerDraggableTouchListener(
        initialPosition: () -> Point,
        positionListener: (x: Int, y: Int) -> Unit
    ) {
        DraggableTouchListener(context, this, initialPosition, positionListener)
    }
}