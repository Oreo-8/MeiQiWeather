package com.example.meiqiweather.fragment

import android.content.Context
import android.widget.FrameLayout
import com.example.meiqiweather.customizeView.DynamicWeatherView
import com.example.meiqiweather.weatherCondition.*
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather
import interfaces.heweather.com.interfacesmodule.view.HeWeather

class WeatherChoose {

    companion object{
        private fun Choose(dwv: DynamicWeatherView, context: Context, weatherCode: String): DynamicWeatherView.WeatherType? {
            return when(weatherCode){
                "100" -> ClearTypeImpl(context, dwv)
                "101" -> CloudyTypeImpl(context, dwv)
                "104" -> OvercastTypeImpl(context, dwv)
                "305","306","307" -> RainTypeImpl(context, dwv)
                "399","400","401","402" -> SnowTypeImpl(context, dwv)
                else -> null
            }
        }

        fun weatherChoose(city: String?,context: Context,frame: FrameLayout) {
            frame.removeAllViews()
            HeWeather.getWeather(context, city, object : HeWeather.OnResultWeatherDataListBeansListener {
                override fun onSuccess(p0: Weather?) {
                    var dwv = DynamicWeatherView(context)
                    dwv.mType = Choose(dwv, context, p0!!.now.cond_code)
                    frame.addView(dwv)
                }

                override fun onError(p0: Throwable?) {

                }
            })
        }
    }

}