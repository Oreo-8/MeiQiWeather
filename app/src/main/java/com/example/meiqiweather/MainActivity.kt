package com.example.meiqiweather

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.example.meiqiweather.adapter.ViewPagerAdapter
import com.example.meiqiweather.customizeView.DynamicWeatherView
import com.example.meiqiweather.weatherCondition.CloudyTypeImpl
import com.example.meiqiweather.data.FragmentWeatherData
import com.example.meiqiweather.data.Resource
import com.example.meiqiweather.fragment.WeatherChoose
import com.example.meiqiweather.fragment.WeatherFragment
import com.example.meiqiweather.weatherCondition.ClearTypeImpl
import com.example.meiqiweather.weatherCondition.OvercastTypeImpl
import com.example.meiqiweather.weatherCondition.RainTypeImpl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather
import interfaces.heweather.com.interfacesmodule.view.HeConfig
import interfaces.heweather.com.interfacesmodule.view.HeWeather
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat as ContextCompat1


class MainActivity : AppCompatActivity(){

    private var adapter: ViewPagerAdapter? = null

    private val gson by lazy { Gson() }

    private val arrayList: ArrayList<Fragment> = ArrayList()

    private val array: ArrayList<FragmentWeatherData> = ArrayList()

    private val type by lazy { object : TypeToken<ArrayList<FragmentWeatherData>>() {}.type }

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private val editor by lazy {  prefs.edit() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //声明 sdk 使用
        HeConfig.init("HE1904151400371526", "16e6a8c497d14b0fa0e4997288b736a8")
        HeConfig.switchToFreeServerNode()

        //权限判断
        permissionJudge()

        //设置标题栏
        val lp = toolbar.layoutParams as FrameLayout.LayoutParams
        lp.setMargins(0, Resource.getStatusBarHeight(this), 0, 0)

        setSupportActionBar(toolbar)
        supportActionBar?.title = " "

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }

        dynamicWeatherView.mType = CloudyTypeImpl(this@MainActivity, dynamicWeatherView)
    }

    private fun init(){
        //清空 碎片集合
        arrayList.clear()
        //增加定位碎片
        arrayList.add(WeatherFragment())
        //判断列表城市数据是否为空
        if (prefs.getString("dataList", null) != null){
            var k = gson.fromJson<ArrayList<FragmentWeatherData>>(prefs.getString("dataList", null), type)
            for (i in 1 until  k.size)
                arrayList.add(WeatherFragment(k[i].city!!))
        }
        adapter = ViewPagerAdapter(supportFragmentManager, arrayList)
        main_ViewPager.adapter = adapter

        main_ViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                var k = gson.fromJson<ArrayList<FragmentWeatherData>>(prefs.getString("dataList", null), type)
                HeWeather.getWeather(this@MainActivity, k[p0].city, object : HeWeather.OnResultWeatherDataListBeansListener {
                    override fun onSuccess(p0: Weather?) {
                        dynamicWeatherView.mType = CloudyTypeImpl(this@MainActivity, dynamicWeatherView)
                        dynamicWeatherView.visibility = View.GONE
                        dynamicWeatherView.visibility = View.VISIBLE
                    }
                    override fun onError(p0: Throwable?) {

                    }
                })
            }

        })
    }

    /**
     * 权限判断
     */
    private fun permissionJudge(){
        val permissionList = ArrayList<String>()
        if (ContextCompat1.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat1.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (ContextCompat1.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionList.isNotEmpty()) {
            val permissions = permissionList.toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, 1)
        } else {
            // 逻辑代码
            init()
        }
    }

    /**
     * 权限判断回调法方
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                }
                // 逻辑代码
                init()
            } else {
                Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.city -> {
                //进入城市管理页面
                val intent = Intent(this, CityManageActivity::class.java)
                //清空城市数据列表
                array.clear()
                //循环碎片列表
                for (i in arrayList){
                    val k = (i as WeatherFragment)
                    //取出碎片城市天气信息列表数据,增加到管理城市列表
                    if (k.mWeather != null){
                        array.add(FragmentWeatherData(k.mWeather?.basic?.location, k.mWeather?.now?.tmp + "°", k.mWeather?.now?.cond_txt))
                    } else {
                        array.add(FragmentWeatherData(k.mCity, k.mTmp, k.mHappening))
                    }
                }
                //传入 管理城市列表数据给 CityManageActivity
                intent.putExtra("data", array)
                startActivityForResult(intent,1)
            }
            R.id.settings -> {

            }
            R.id.details -> {

            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.getBooleanExtra("flag", false)) {
                        arrayList.clear()
                        // 当CityManageActivity的列表点击事件触发时
                        // 取出储存器的数据
                        var k = gson.fromJson<ArrayList<FragmentWeatherData>>(prefs.getString("dataList", null), type)
                        arrayList.add(WeatherFragment())
                        // 将储存器的数据取出添加相应碎片

                        for (i in 1 until k.size) {
                            arrayList.add(WeatherFragment(k[i].city!!))
                        }

                        //刷新适配器
                        adapter?.notifyDataSetChanged()
                    }

                    //跳转页面
                    main_ViewPager.currentItem = data!!.getIntExtra("choose", 0)
                }
            }
        }
    }

}
