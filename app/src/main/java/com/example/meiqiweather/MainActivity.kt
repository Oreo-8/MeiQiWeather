package com.example.meiqiweather

import android.Manifest
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.Toast
import com.example.meiqiweather.adapter.ViewPagerAdapter
import com.example.meiqiweather.customizeView.DynamicWeatherView
import com.example.meiqiweather.data.FragmentWeatherData
import com.example.meiqiweather.data.Resource
import com.example.meiqiweather.fragment.WeatherChoose
import com.example.meiqiweather.fragment.WeatherFragment
import com.example.meiqiweather.service.WeatherJob
import com.example.meiqiweather.service.WeatherService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import interfaces.heweather.com.interfacesmodule.view.HeConfig
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_list.view.*
import java.util.concurrent.TimeUnit
import android.support.v4.content.ContextCompat as ContextCompat1

class MainActivity : AppCompatActivity(){

    private var adapter: ViewPagerAdapter? = null

    private val gson by lazy { Gson() }

    private val arrayList: ArrayList<Fragment> = ArrayList()

    private val array: ArrayList<FragmentWeatherData> = ArrayList()

    private val type by lazy { object : TypeToken<ArrayList<FragmentWeatherData>>() {}.type }

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private val items = arrayOf("晴天", "多云", "阴天", "雨天", "雪天")
    private val code = arrayOf("100", "101", "104", "305", "399")
    private val image = intArrayOf(R.drawable.ic_clear, R.drawable.ic_cloudy, R.drawable.ic_overcast,
        R.drawable.ic_rain, R.drawable.ic_snow)

    private val previewArrayList = ArrayList<HashMap<String, Any>>()

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

//        val startIntent = Intent(this, WeatherService::class.java)
//        //启动服务
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val builder =  JobInfo.Builder(1, ComponentName(this, WeatherJob::class.java))
//            val jobScheduler = this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//            builder.setPeriodic(1000 * 60*15)//设置延迟调度时间
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)//设置所需网络类型
//            builder.setRequiresCharging(true)//设置在充电时执行Job
//            builder.setRequiresDeviceIdle(false)
//            jobScheduler.schedule(builder.build())
//        } else {
//            startService(startIntent)
//        }

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        val startIntent = Intent(this, WeatherService::class.java)
//        stopService(startIntent)
//    }

    private fun init(){
        //清空 碎片集合
        arrayList.clear()
        //增加定位碎片
        arrayList.add(WeatherFragment())
        //判断列表城市数据是否为空
        if (prefs.getString("dataList", null) != null){
            val k = gson.fromJson<ArrayList<FragmentWeatherData>>(
                prefs.getString("dataList", null), type)
            for (i in 1 until  k.size)
                arrayList.add(WeatherFragment(k[i].city!!, i))
        }
        adapter = ViewPagerAdapter(supportFragmentManager, arrayList)
        main_ViewPager.adapter = adapter
        main_ViewPager.offscreenPageLimit = arrayList.size - 1

        //预览天气效果
        previewArrayList.clear()
        for (i in  0 until items.size){
            val listItem = HashMap<String, Any>()
            listItem["items"] = items[i]
            listItem["code"] = code[i]
            listItem["image"] = image[i]
            previewArrayList.add(listItem)
        }
    }

    /**
     * 权限判断
     */
    private fun permissionJudge(){
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionList = ArrayList<String>()
        for (p in permissions){
            if (ContextCompat1.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(p)
            }
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

    /**
     * 设置菜单栏
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    /**
     * 菜单栏事件
     */
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
                        array.add(FragmentWeatherData(k.mWeather?.basic?.location,
                            k.mWeather?.now?.tmp + "°", k.mWeather?.now?.cond_txt,
                            k.mWeather?.now?.cond_code))
                    }
                }
                //传入 管理城市列表数据给 CityManageActivity
                intent.putExtra("data", array)
                startActivityForResult(intent,1)
            }
            R.id.settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivityForResult(intent,2)
            }
            R.id.preview ->{
                val v = layoutInflater.inflate(R.layout.dialog_list, null)
                //使用简单适配器
                val simpleAdapter = SimpleAdapter(
                    this, previewArrayList, R.layout.dialog_item,
                    arrayOf("items", "image"),
                    intArrayOf(R.id.text, R.id.image)
                )
                v.listView.adapter = simpleAdapter
                //设置对话框
                val alertDialog = AlertDialog.Builder(this)
                    .setView(v)
                    .create()
                alertDialog.show()
                //设置对话框大小
                alertDialog.window.setLayout(700, LinearLayout.LayoutParams.WRAP_CONTENT)
                val window = alertDialog.window
                val lp = window.attributes
                //设置对话框透明度
                lp.alpha = 0.7f
                window.attributes = lp
                //注册 listView 事件
                v.listView.setOnItemClickListener { _, _, position, _ ->
                    val map = previewArrayList[position]
                    frame.removeAllViews()
                    val dwv = DynamicWeatherView(this)
                    dwv.mType = (WeatherChoose.choose(dwv, this, map["code"].toString()))
                    frame.addView(dwv)
                    alertDialog.dismiss()
                }
            }
            R.id.details -> {
                val intent = Intent(this, DetailActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    /**
     * 活动信息回调法方
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.getBooleanExtra("flag", false)) {
                        arrayList.clear()
                        arrayList.add(WeatherFragment())

                        // 当CityManageActivity的列表点击事件触发时
                        // 取出储存器的数据
                        val k = gson.fromJson<ArrayList<FragmentWeatherData>>(
                            prefs.getString("dataList", null), type)
                        // 将储存器的数据取出添加相应碎片

                        for (i in 1 until k.size) {
                            arrayList.add(WeatherFragment(k[i].city!!, i))
                        }
                        //刷新适配器
                        adapter?.notifyDataSetChanged()
                    }

                    //跳转页面
                    main_ViewPager.currentItem = data.getIntExtra("choose", 0)
                }
            }
            2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    (arrayList[main_ViewPager.currentItem] as WeatherFragment).settingJudgment()
                }
            }
        }
    }

}
