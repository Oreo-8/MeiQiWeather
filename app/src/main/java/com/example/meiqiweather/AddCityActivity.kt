package com.example.meiqiweather

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import com.example.meiqiweather.adapter.AddCityAdapter
import interfaces.heweather.com.interfacesmodule.bean.Lang
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic
import interfaces.heweather.com.interfacesmodule.bean.search.Search
import interfaces.heweather.com.interfacesmodule.view.HeWeather
import kotlinx.android.synthetic.main.activity_add_city.*

class AddCityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_city)
        //设置标题栏
        setSupportActionBar(addCity_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //键盘确定事件
        addCity_eText.setOnKeyListener { _, keyCode, event ->
            if (KeyEvent.KEYCODE_ENTER == keyCode && event.action == KeyEvent.ACTION_DOWN) {
                //执行搜索城市
                citySearch(addCity_eText.text.toString())
                true
            }
            false
        }

        //文本框输入事件
        addCity_eText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?){}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //执行搜索城市
                citySearch(addCity_eText.text.toString())
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun citySearch(cityKey: String){
        HeWeather.getSearch(this, cityKey, "CN", 20, Lang.CHINESE_SIMPLIFIED, object : HeWeather.OnResultSearchBeansListener{
            override fun onSuccess(p0: Search?) {
                if (p0 != null){
                    //将数据传入显示列表
                    val layoutManager = LinearLayoutManager(this@AddCityActivity)
                    add_list.layoutManager = layoutManager
                    var adapter = AddCityAdapter(p0.basic)
                    add_list.adapter = adapter
                    //列表回调事件
                    adapter.setOnItemClickListener(itemClickListener)
                }
            }

            override fun onError(p0: Throwable?) = p0!!.printStackTrace()
        })
    }

    val itemClickListener = object: AddCityAdapter.ItemClickListener{
        override fun onItemClickListener(basic: Basic) {
            val intent = Intent()
            //传当前点击项的城市地址到 CityManageActivity
            intent.putExtra("city", basic.location)
            setResult(Activity.RESULT_OK, intent)
            //结束活动
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode){
            KeyEvent.KEYCODE_BACK -> {
                finish()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}
