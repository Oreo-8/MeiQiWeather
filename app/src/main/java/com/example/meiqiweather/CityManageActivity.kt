package com.example.meiqiweather

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.example.meiqiweather.adapter.CityAdapter
import com.example.meiqiweather.data.FragmentWeatherData
import com.example.meiqiweather.data.Resource
import com.example.meiqiweather.fragment.WeatherFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now
import interfaces.heweather.com.interfacesmodule.view.HeWeather
import kotlinx.android.synthetic.main.activity_add_city.*
import kotlinx.android.synthetic.main.activity_city_manage.*
import kotlinx.android.synthetic.main.activity_main.*


class CityManageActivity : AppCompatActivity() {

    private val gson = Gson()

    private var flag = false

    private var mData: ArrayList<FragmentWeatherData> = ArrayList()

    private val type by lazy { object : TypeToken<ArrayList<FragmentWeatherData>>() {}.type }

    private lateinit var adapter: CityAdapter

    private val inten: Intent = Intent()

    private val prefs by lazy { PreferenceManager.
        getDefaultSharedPreferences(this) }

    private val editor by lazy { prefs.edit() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_manage)

        val lp = cityManage_toolbar.layoutParams as LinearLayout.LayoutParams
        lp.setMargins(0, Resource.getStatusBarHeight(this), 0, 0)
        setSupportActionBar(cityManage_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }

        cityManage_fab.setOnClickListener {
            //接收下一个活动的数据
            startActivityForResult(Intent(this, AddCityActivity::class.java),2)
        }

        mData = if (prefs.getString("dataList", null) != null){
            gson.fromJson<ArrayList<FragmentWeatherData>>(prefs.getString("dataList", null), type)
        } else{
            intent.getSerializableExtra("data") as ArrayList<FragmentWeatherData>
        }

        //城市管理列表
        val layoutManager = LinearLayoutManager(this)
        recycler_city.layoutManager = layoutManager
        adapter = CityAdapter(mData)
        recycler_city.adapter = adapter
        touchHelper.attachToRecyclerView(recycler_city)

        //适配器回调单击事件
        adapter.setOnItemClickListener(object: CityAdapter.ItemClickListener{
            override fun onItemClickListener(position: Int) {
                //点击发送返回 下标 到MainActivity
                inten.putExtra("choose", position)
                inten.putExtra("flag", flag)
                setResult(Activity.RESULT_OK, inten)
                //结束活动
                finish()
            }
        } )

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                //点击发送返回 下标 到MainActivity
                inten.putExtra("flag", flag)
                setResult(Activity.RESULT_OK, inten)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            2 -> {
                if (resultCode == Activity.RESULT_OK){
                    // 当 AddCityActivity 的列表点击事件触发时
                    // 取出 AddCityActivity 传过来的数据
                    var city =  data!!.getStringExtra("city")
                    HeWeather.getWeatherNow(this, city, object : HeWeather.OnResultWeatherNowBeanListener{
                        override fun onSuccess(p0: Now?) {
                            if (p0 != null){
                                flag = true
                                // 把数据增到  mData
                                mData.add(FragmentWeatherData(city, "${p0.now.tmp}°", p0.now.cond_txt))
                                // 重新储存数据
                                val json = gson.toJson(mData)
                                editor.putString("dataList", json)
                                editor.apply()
                                //刷新列表适配器
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onError(p0: Throwable?) = p0!!.printStackTrace()

                    })

                }
            }
        }
    }

    // 滑动删除城市
    private val touchHelper = ItemTouchHelper(object : ItemTouchHelper
    .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p1: Int) {
            var pos = viewHolder.adapterPosition
            if (pos != 0) {
                flag = true
                editor.remove(mData[pos].city)
                mData.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                val json = gson.toJson(mData)
                editor.putString("dataList", json)
                editor.apply()
            }
        }

        //滑动动画
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (viewHolder.adapterPosition != 0) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变 Item 的透明度，以实现滑动过程中实现渐变效果
                    val alpha = 1 - Math.abs(dX) / viewHolder.itemView.width.toFloat()
                    viewHolder.itemView.alpha = alpha
                    viewHolder.itemView.translationX = dX
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        }

    })

    //返回键事件
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode){
            KeyEvent.KEYCODE_BACK -> {
                inten.putExtra("flag", flag)
                setResult(Activity.RESULT_OK, inten)
                finish()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}
