package com.example.meiqiweather.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.meiqiweather.R
import com.example.meiqiweather.adapter.HourlyBaseAdapter
import com.example.meiqiweather.customizeView.DynamicWeatherView
import com.example.meiqiweather.data.*
import com.example.meiqiweather.weatherCondition.ClearTypeImpl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather
import interfaces.heweather.com.interfacesmodule.view.HeWeather
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.future_layout.view.*
import kotlinx.android.synthetic.main.life_index_layout.view.*
import kotlinx.android.synthetic.main.life_item.view.*
import kotlinx.android.synthetic.main.line_layout.view.*
import kotlinx.android.synthetic.main.weather_fragment.view.*
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ValidFragment")
class WeatherFragment() : Fragment() ,AMapLocationListener{

    var mCity: String? = null

    var mTmp: String? = null

    var mHappening: String? = null

    private var cityCode: String? = null

    constructor(cityCode: String): this(){
        this.cityCode = cityCode
    }

    private var positioningCode: String? = null

    private val type by lazy { object : TypeToken<Mweather>() {}.type }

    private val gson by lazy {  Gson() }

    private val up by lazy { intArrayOf(1,1,1,1,1,1) }

    private val down by lazy { intArrayOf(0,0,0,0,0,0) }

    private lateinit var v: View

    private var mlocationClient: AMapLocationClient? = null

    private var mLocationOption: AMapLocationClientOption? = null

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private val editor by lazy {  prefs.edit() }

    var mWeather: Mweather? = null

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            mWeather = gson.fromJson<Mweather>(prefs.getString(msg!!.obj.toString(), null), type)
            updateComponent(mWeather!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.weather_fragment, container, false)

        //设置刷新颜色与监听
        v.main_SwipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        v.main_SwipeRefresh.setOnRefreshListener { refreshWeather() }

        //设置与下拉刷新的兼容
        v.appBar.addOnOffsetChangedListener(object : AppBarLayoutStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    State.EXPANDED -> {
                        v.main_SwipeRefresh.isEnabled = true
                        activity!!.frame.visibility = View.VISIBLE
                    }
                    State.COLLAPSED -> {
                        v.main_SwipeRefresh.isEnabled = false
                        activity!!.frame.visibility = View.GONE
                    }
                    State.INTERMEDIATE -> {
                        v.main_SwipeRefresh.isEnabled = false
                    }
                }
            }

        })


        //设置星期
        val dayText by lazy {
            arrayListOf<TextView>(v.future_layout.day_1,v.future_layout.day_2,
            v.future_layout.day_3, v.future_layout.day_4, v.future_layout.day_5, v.future_layout.day_6)
        }
        for (i in 1 until dayText.size)
            dayText[i].text = week(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+i)

        v.toolbar.title = ""
        //设定位

        if (cityCode != null){
            storageJudgment(cityCode!!)
            assignment(cityCode!!)
        } else {
            mlocationClient = AMapLocationClient(activity)
            mLocationOption = AMapLocationClientOption()
            mlocationClient?.setLocationListener(this)
            mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mlocationClient?.setLocationOption(mLocationOption)
            mlocationClient?.startLocation()
        }

        return v
    }

    private fun assignment(city: String){
        HeWeather.getWeather(context, city, object : HeWeather.OnResultWeatherDataListBeansListener {
            override fun onSuccess(weather: Weather?) {
                if (weather != null) {
                    mTmp = weather.now?.tmp + "°"
                    mHappening = weather.now?.cond_txt
                    mCity = weather.basic?.location
                }
            }

            override fun onError(p0: Throwable?) = p0!!.printStackTrace()
        })
    }


    /**
     * 刷新天气
     */
    private fun refreshWeather(){
        Thread(Runnable {
            //执行刷新代码
            if (cityCode != null){
                cityCode?.let { assignment(it) }
                cityCode?.let { updateWeather(it) }
            }else{
                mlocationClient?.startLocation()
                positioningCode?.let { updateWeather(it) }
            }
            activity?.runOnUiThread{
                v.main_SwipeRefresh.isRefreshing = false
            }

        }).start()
    }

    /**
     * 定位并获取天气信息更新组件
     */
    override fun onLocationChanged(p0: AMapLocation?) {
        if (p0 != null){
            if (p0.errorCode == 0){
                positioningCode = p0.adCode
                //传入城市码
                assignment(p0.adCode)
                storageJudgment(p0.adCode)
                v.toolbar.setNavigationIcon(R.mipmap.ic_locate_city)
                //停止定位
                editor.putString("c", p0.adCode)
                editor.apply()
                mlocationClient?.stopLocation()
            }
        }else{
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                        + "${p0?.errorCode} , errInfo: ${p0?.errorInfo}")
        }
    }

    private fun storageJudgment(city: String){
        mWeather = gson.fromJson<Mweather>(prefs.getString(city, null), type)
        if (mWeather == null) {
            Thread{
                updateWeather(city)
            }.start()
        }else{
            updateComponent(mWeather!!)
        }
    }

    /**
     * 获取天气信息更新组件
     */
    private fun updateWeather(city: String) {
        HeWeather.getWeather(context, city, object : HeWeather.OnResultWeatherDataListBeansListener {
            override fun onSuccess(weather: Weather?) {
                if (weather != null) {

                    var now = Now(weather.now.tmp, weather.now.cond_txt,
                        weather.now.cond_code, weather.now.hum, weather.now.pres,
                        weather.now.wind_sc, weather.now.wind_dir)
                    var basic = Basic(weather.basic.location, weather.update.loc)
                    var lifestyle = ArrayList<Lifestyle>()
                    var hourly = ArrayList<Hourly>()
                    var daily_forecast = ArrayList<DailyForecast>()
                    var sun = Sun(weather.daily_forecast[0].sr, weather.daily_forecast[0].ss)

                    for (i in weather.hourly){
                        hourly.add(Hourly(i.cond_code, i.time, i.tmp))
                    }
                    for (i in 0 until weather.daily_forecast.size-1) {
                        daily_forecast.add(DailyForecast(
                            weather.daily_forecast[i].tmp_max,
                            weather.daily_forecast[i].tmp_min,
                            weather.daily_forecast[i].cond_code_d,
                            weather.daily_forecast[i].cond_txt_d,
                            weather.daily_forecast[i].date))
                    }
                    for (i in weather.lifestyle) {
                        lifestyle.add(Lifestyle(i.type, i.txt, i.brf))
                    }


                    val mWeather = Mweather(now, daily_forecast, basic, hourly, lifestyle, sun, city)

                    val json = gson.toJson(mWeather)
                    editor.putString(city, json)
                    editor.apply()

                    var message = Message()
                    message.obj = city
                    handler.handleMessage(message)
                }
            }

            //异常处理
            override fun onError(p0: Throwable?) = p0!!.printStackTrace()
        })

    }

    @SuppressLint("SetTextI18n")
    private fun updateComponent(mWeather: Mweather){
        //设置当前温度和标题栏定位地址

        v.fragment_temp.text = mWeather.now?.tmp + "°"
        v.fragment_happening.text = mWeather.now?.cond_txt
        v.max_temp.text = "↟ " + mWeather.daily_forecast[0].tmp_max + "℃"
        v.min_temp.text = "↡ " + mWeather.daily_forecast[0].tmp_min + "℃"
        v.toolbar.title = mWeather.basic?.location
        v.toolbar.subtitle = mWeather.basic?.loc?.substring(11)

        //设置天气预报6天折线图
        val imageView by lazy {
            arrayListOf(
                v.future_layout.icon_1, v.future_layout.icon_2, v.future_layout.icon_3,
                v.future_layout.icon_4, v.future_layout.icon_5, v.future_layout.icon_6
            )
        }
        val dateText by lazy {
            arrayListOf(
                v.future_layout.date_1, v.future_layout.date_2, v.future_layout.date_3,
                v.future_layout.date_4, v.future_layout.date_5, v.future_layout.date_6
            )
        }
        val situationText by lazy {
            arrayListOf(
                v.future_layout.situation_1, v.future_layout.situation_2, v.future_layout.situation_3,
                v.future_layout.situation_4, v.future_layout.situation_5, v.future_layout.situation_6
            )
        }
        for (i in 0 until mWeather.daily_forecast.size ) {
            up[i] = (mWeather.daily_forecast[i].tmp_max).toInt()
            down[i] = (mWeather.daily_forecast[i].tmp_min).toInt()
            Resource.hashMap[mWeather.daily_forecast[i].cond_code_d]?.let { imageView[i].setImageResource(it) }
            dateText[i].text =
                "${(mWeather.daily_forecast[i].date)[5]}${(mWeather.daily_forecast[i].date)[6]}/" +
                        "${(mWeather.daily_forecast[i].date)[8]}${(mWeather.daily_forecast[i].date)[9]}"
            situationText[i].text = mWeather.daily_forecast[i].cond_txt_d
        }
        //对自定义折线图传入两对数组
        v.line_layout.trend_graph.setMElements(up, down)

        //每小时预报
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        v.fragment_recycler.layoutManager = layoutManager
        val adapter = HourlyBaseAdapter(mWeather.hourly, Resource.hashMap)
        v.fragment_recycler.adapter = adapter

        //设置中间温度、气压、风级
        v.hum.text = mWeather.now?.hum + "%"
        v.pres.text = mWeather.now?.pres
        v.wind_sc.text = mWeather.now?.wind_sc + "级"
        v.wind_dir.text = mWeather.now?.wind_dir

        //设置生活指数
        v.life_index_layout.life_layout.removeAllViews()
        for (i in mWeather.lifestyle) {
            val l = layoutInflater.inflate(R.layout.life_item, null)
            l.circle_image.setImageDrawable(context?.let {
                ContextCompat.getDrawable(
                    it,
                    Resource.lifeData[i.type]!!.dateImage
                )
            })
            Resource.lifeData[i.type]?.dateBackground?.let { l.circle_image.setBackgroundResource(it) }
            l.life_text.text = "${Resource.lifeData[i.type]!!.dateTitle}: ${i.brf}"
            l.life_text2.text = i.txt
            v.life_index_layout.life_layout.addView(l)
        }

        //显示组件
        v.fragment_linear.visibility = View.VISIBLE
        v.weather_relative.visibility = View.VISIBLE
    }

    //设置星期String
    private fun week(i: Int): String {
        val str = arrayOf("", "周日", "周一", "周二", "周三", "周四", "周五", "周六")
        if (i > 7) {
            return str[i - 7]
        }
        return str[i]
    }

}