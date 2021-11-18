package com.extend.englishShots

import android.app.ActivityManager
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.extend.englishShots.Services.ForegroundService
import com.extend.englishShots.Utils.Const
import com.hbb20.CountryPickerView
import com.hbb20.countrypicker.models.CPCountry
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var transitionCheckbox4s: CheckBox? = null
    private var transitionCheckbox8s: CheckBox? = null
    private var transitionCheckbox12s: CheckBox? = null
    private var transitionCheckbox16s: CheckBox? = null

    private var disappearCheckBox4s: CheckBox? = null
    private var disappearCheckBox8s: CheckBox? = null
    private var disappearCheckBox12s: CheckBox? = null
    private var disappearCheckBox16s: CheckBox? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()


        if (!Settings.canDrawOverlays(this)) {
            showDialog(getString(R.string.draw_over_apps_permission))
        }

        if (!isMyServiceRunning()) {
            ForegroundService.startService(this, getString(R.string.foreground_service_sub_title))
            Log.i("TAG", "this will be called immediately")
        } else {
            ForegroundService.stopService(this)
            ForegroundService.startService(this, getString(R.string.foreground_service_sub_title))
        }

    }

    private fun initView() {
        sharedPreferences = getSharedPreferences(Const.PREF_KEY, MODE_PRIVATE)
        transitionCheckbox4s = findViewById(R.id.origin_lang_checkbox_4s)
        transitionCheckbox8s = findViewById(R.id.origin_lang_checkbox_8s)
        transitionCheckbox12s = findViewById(R.id.origin_lang_checkbox_12s)
        transitionCheckbox16s = findViewById(R.id.origin_lang_checkbox_16s)

        disappearCheckBox4s = findViewById(R.id.translation_lang_checkbox_4s)
        disappearCheckBox8s = findViewById(R.id.translation_lang_checkbox_8s)
        disappearCheckBox12s = findViewById(R.id.translation_lang_checkbox_12s)
        disappearCheckBox16s = findViewById(R.id.translation_lang_checkbox_16s)
        when (sharedPreferences!!.getLong(Const.TIMEOUT_DISSAPEAR, 4L)) {
            4L -> disappearCheckBox4s!!.isChecked = true
            8L -> disappearCheckBox8s!!.isChecked = true
            12L -> disappearCheckBox12s!!.isChecked = true
            16L -> disappearCheckBox16s!!.isChecked = true
        }
        when (sharedPreferences!!.getLong(Const.TIMEOUT_TRANSITION, 4L)) {
            4L -> transitionCheckbox4s!!.isChecked = true
            8L -> transitionCheckbox8s!!.isChecked = true
            12L -> transitionCheckbox12s!!.isChecked = true
            16L -> transitionCheckbox16s!!.isChecked = true
        }

        setListeners()
        val countryPicker = findViewById<CountryPickerView>(R.id.countryPicker)

        countryPicker.cpViewHelper.cpViewConfig.viewTextGenerator = { cpCountry: CPCountry ->
            "${cpCountry.name} (${cpCountry.alpha2})"
        }
        countryPicker.cpViewHelper.onCountryChangedListener = { selectedCountry: CPCountry? ->
            sharedPreferences!!.edit().putString(Const.SELECTED_LANGUAGE, selectedCountry?.name)
                .apply()
        }
        val list = arrayListOf<CPCountry>()
        for (item in countryPicker.cpViewHelper.cpDataStore.countryList) {
            if (item.englishName != Const.EN_NAME
                && item.englishName != Const.FR_NAME
                && item.englishName != Const.DE_NAME
            ) {
                list.add(item)
            }
        }

        countryPicker.cpViewHelper.cpDataStore.countryList.removeAll(list)
        countryPicker.cpViewHelper.refreshView()
    }

    private fun setListeners() {

        transitionCheckbox4s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                transitionCheckbox8s!!.isChecked = false
                transitionCheckbox12s!!.isChecked = false
                transitionCheckbox16s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_TRANSITION, 4)?.apply()
            }
        }

        transitionCheckbox8s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                transitionCheckbox4s!!.isChecked = false
                transitionCheckbox12s!!.isChecked = false
                transitionCheckbox16s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_TRANSITION, 8)?.apply()
            }
        }


        transitionCheckbox12s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                transitionCheckbox8s!!.isChecked = false
                transitionCheckbox4s!!.isChecked = false
                transitionCheckbox16s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_TRANSITION, 12)?.apply()
            }
        }


        transitionCheckbox16s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                transitionCheckbox8s!!.isChecked = false
                transitionCheckbox12s!!.isChecked = false
                transitionCheckbox4s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_TRANSITION, 16)?.apply()
            }
        }

        disappearCheckBox4s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                disappearCheckBox8s!!.isChecked = false
                disappearCheckBox12s!!.isChecked = false
                disappearCheckBox16s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_DISSAPEAR, 4)?.apply()
            }
        }
        disappearCheckBox8s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                disappearCheckBox4s!!.isChecked = false
                disappearCheckBox12s!!.isChecked = false
                disappearCheckBox16s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_DISSAPEAR, 8)?.apply()
            }
        }

        disappearCheckBox12s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                disappearCheckBox8s!!.isChecked = false
                disappearCheckBox4s!!.isChecked = false
                disappearCheckBox16s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_DISSAPEAR, 12)?.apply()
            }
        }
        disappearCheckBox16s!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                disappearCheckBox8s!!.isChecked = false
                disappearCheckBox12s!!.isChecked = false
                disappearCheckBox4s!!.isChecked = false
                sharedPreferences?.edit()?.putLong(Const.TIMEOUT_DISSAPEAR, 16)?.apply()
            }
        }
    }

    private fun showDialog(title: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        val body = dialog.findViewById(R.id.popup_dialog) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.yes_opt) as TextView
        val noBtn = dialog.findViewById(R.id.no_opt) as TextView
        yesBtn.setOnClickListener {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            exitProcess(0)
        }
        dialog.show()

    }

    private fun isMyServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (ForegroundService::class.simpleName == service.service.className) {
                return true
            }
        }
        return false
    }
}
