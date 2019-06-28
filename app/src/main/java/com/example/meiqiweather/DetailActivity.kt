package com.example.meiqiweather

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.example.meiqiweather.data.Resource
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val lp = detail_toolbar.layoutParams as LinearLayout.LayoutParams
        lp.setMargins(0, Resource.getStatusBarHeight(this), 0, 0)
        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }

        val alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.view_animation)
        ic_image.startAnimation(alphaAnimation)
        t1.startAnimation(alphaAnimation)
        t2.startAnimation(alphaAnimation)
        t3.startAnimation(alphaAnimation)

        t2.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}
