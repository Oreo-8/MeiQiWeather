package com.example.meiqiweather.data

import java.io.Serializable

data class Data(val dateTitle: String, val dateImage: Int, val dateBackground: Int)

data class FragmentWeatherData(val city: String?, val tmp: String?, val happening: String?): Serializable

data class Elements(val up: Int, val down: Int)

data class DailyForecast(val tmp_max: String, val tmp_min: String, val cond_code_d: String,
                         val cond_txt_d: String, val date: String): Serializable

data class Now(val tmp: String, val cond_txt: String, val cond_code: String, val hum: String, val pres: String,
               val wind_sc: String, val wind_dir: String): Serializable

data class Basic(val location: String, val loc: String): Serializable

data class Hourly(val cond_code: String, val time: String, val tmp: String): Serializable

data class Lifestyle(val type: String, val txt: String, val brf: String): Serializable

data class Sun(val sr: String, val ss: String): Serializable

data class Mweather(var now: Now? = null, var daily_forecast: ArrayList<DailyForecast> = ArrayList(),
                    var basic: Basic? = null, var hourly: ArrayList<Hourly>  = ArrayList(),
                   var lifestyle: ArrayList<Lifestyle> = ArrayList(), var sun: Sun? = null, var city: String): Serializable

