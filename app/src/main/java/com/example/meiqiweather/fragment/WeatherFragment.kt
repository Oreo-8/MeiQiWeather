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
import com.example.meiqiweather.fragment.WeatherChoose.Companion.choose
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

class WeatherFragment() : Fragment() ,AMapLocationListener{

    private var cityCode: String? = null

    private var mId: Int = 0

    private var isInit = false
    private var isLoad = false

    @SuppressLint("ValidFragment")
    constructor(cityCode: String, id: Int): this(){
        this.cityCode = cityCode
        this.mId = id
    }

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

    private var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            mWeather = gson.fromJson<Mweather>(prefs.getString(msg!!.obj.toString(), null), type)
            updateComponent(mWeather!!)
            isCanLoadData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.weather_fragment, container, false)

        //里面开始对页面进行数据加载
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

        isInit = true
        isCanLoadData()

        return v
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isCanLoadData()
    }

      /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private fun isCanLoadData() {
        //视图没有初始化
        if (!isInit) {
            return
        }
        //判断视图对用户是否可见
        if (userVisibleHint) {
            lazyLoad()
            isLoad = true
        } else {
            if (isLoad) {
                stopLoad()
            }
        }
    }

      /**
     * 当视图初始化并对用户可见的时候去真正的加载数据
     */
    private fun lazyLoad() {

          if (cityCode != null){
              storageJudgment(cityCode!!)
          } else {
              v.toolbar.setNavigationIcon(R.mipmap.ic_locate_city)
              var code = prefs.getString("adCode", null)
              if (code != null){
                  storageJudgment(code)
              }else {
                  mlocationClient = AMapLocationClient(activity)
                  mLocationOption = AMapLocationClientOption()
                  mlocationClient?.setLocationListener(this)
                  mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                  mlocationClient?.setLocationOption(mLocationOption)
                  mlocationClient?.startLocation()
              }
          }

    }

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    private fun stopLoad() {
      //让已经在加载过数据并不可见的页面停止加载（例如 视频播放时切换过去不可见时，要让它停止播放）
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isInit = false
        isLoad = false
    }

    /**
     * 刷新天气
     */
    fun refreshWeather(){
        Thread(Runnable {
            //执行刷新代码
            if (cityCode != null){
                cityCode?.let { updateWeather(it,1) }
            }else{
                mlocationClient?.startLocation()
                prefs.getString("adCode", null)?.let { updateWeather(it,1) }
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
                //传入城市码
                storageJudgment(p0.adCode)
                //停止定位
                editor.putString("adCode", p0.adCode)
                editor.apply()
                mlocationClient?.stopLocation()
            }
        }else{
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                        + "${p0?.errorCode} , errInfo: ${p0?.errorInfo}")
        }
    }

    /**
     *  判断有无缓存 如果有缓存就读取缓存信息，无则去获取天气信息
     */
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
     * 获取天气信息并储存
     */
    private fun updateWeather(city: String, type: Int = 0) {
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

                    val mWeather = Mweather(now, daily_forecast, basic, hourly, lifestyle, sun, weather.now.cond_code)

                    var json = gson.toJson(mWeather)
                    editor.putString(city, json)
                    editor.apply()

                    if (type == 1) {
                        var k = gson.fromJson<ArrayList<FragmentWeatherData>>(
                            prefs.getString("dataList", null),
                            object : TypeToken<ArrayList<FragmentWeatherData>>() {}.type
                        )
                        k[mId].happening = weather.now?.cond_txt
                        k[mId].tmp = weather.now?.tmp + "°"

                        var json = gson.toJson(k)
                        editor.putString("dataList", json)
                        editor.apply()
                    }


                    var message = Message()
                    message.obj = city
                    handler.handleMessage(message)
                }
            }

            //异常处理
            override fun onError(p0: Throwable?) = p0!!.printStackTrace()
        })

    }

    /**
     * 读取储存更新组件
     */
    @SuppressLint("SetTextI18n")
    private fun updateComponent(mWeather: Mweather, type: Int = 1){

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

        if (type == 1) {
            activity!!.frame.removeAllViews()
            var dwv = DynamicWeatherView(context!!)
            dwv.mType = choose(dwv, context!!, mWeather.cond_code)
            activity!!.frame.addView(dwv)
        }

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