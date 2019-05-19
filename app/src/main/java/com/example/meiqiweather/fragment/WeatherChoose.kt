package com.example.meiqiweather.fragment

import android.content.Context
import com.example.meiqiweather.customizeView.DynamicWeatherView
import com.example.meiqiweather.weatherCondition.*

class WeatherChoose {

    companion object{
        fun Choose(dwv: DynamicWeatherView, context: Context, weatherCode: String){
            dwv.mType = when(weatherCode){
                "100" -> ClearTypeImpl(context, dwv)
                "101" -> CloudyTypeImpl(context, dwv)
                "104" -> OvercastTypeImpl(context, dwv)
                "305","306","307" -> RainTypeImpl(context, dwv)
                "399","400","401","402" -> SnowTypeImpl(context, dwv)
                else -> null
            }
        }
    }

}