package com.example.meiqiweather.fragment

import android.content.Context
import com.example.meiqiweather.R
import com.example.meiqiweather.customizeView.DynamicWeatherView
import com.example.meiqiweather.weatherCondition.*

class WeatherChoose {

    companion object{
        fun choose(dwv: DynamicWeatherView, context: Context, weatherCode: String): DynamicWeatherView.WeatherType? {
            return when(weatherCode){
                "100" -> ClearTypeImpl(context, dwv)
                "101" -> CloudyTypeImpl(context, dwv)
                "104" -> OvercastTypeImpl(context, dwv)
                "305","306","307" -> RainTypeImpl(context, dwv)
                "399","400","401","402" -> SnowTypeImpl(context, dwv)
                else -> null
            }
        }

        fun chooseBack(weatherCode: String): Int{
            return when(weatherCode){
                "100" -> R.drawable.clear_sky
                "101" -> R.drawable.cloudy_sky
                "104" -> R.drawable.overcast_sky
                "305","306","307" -> R.drawable.rain_sky
                "399","400","401","402" -> R.drawable.snow_sky
                else -> R.drawable.overcast_sky
            }
        }
    }

}