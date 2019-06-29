package com.example.meiqiweather

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import com.example.meiqiweather.data.Resource
import com.example.meiqiweather.data.SettingStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private val editor by lazy {  prefs.edit() }

    private val gson by lazy {  Gson() }

    private val typeSS by lazy { object : TypeToken<SettingStatus>() {}.type }

    private val inten: Intent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val lp = setting_toolbar.layoutParams as LinearLayout.LayoutParams
        lp.setMargins(0, Resource.getStatusBarHeight(this), 0, 0)
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }

        val  ss = gson.fromJson<SettingStatus>(prefs.getString("SettingStatus", null), typeSS)
        if (ss != null){
            switch_1.isChecked = ss.forecast
            switch_2.isChecked = ss.sun
        } else {
            val json = gson.toJson(SettingStatus())
            editor.putString("SettingStatus", json)
            editor.apply()
        }
        switch_1.setOnCheckedChangeListener(this)
        switch_2.setOnCheckedChangeListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                setResult(Activity.RESULT_OK, inten)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //返回键事件
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode){
            KeyEvent.KEYCODE_BACK -> {
                setResult(Activity.RESULT_OK, inten)
                finish()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val  ss = gson.fromJson<SettingStatus>(prefs.getString("SettingStatus", null), typeSS)
        when(buttonView?.id){
            R.id.switch_1 ->{
                ss.forecast = isChecked
            }
            R.id.switch_2 ->{
                ss.sun = isChecked
            }
        }
        val json = gson.toJson(ss)
        editor.putString("SettingStatus", json)
        editor.apply()

    }

}
