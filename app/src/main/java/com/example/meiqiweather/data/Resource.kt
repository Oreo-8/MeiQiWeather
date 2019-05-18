package com.example.meiqiweather.data

import android.app.Activity
import com.example.meiqiweather.MainActivity
import com.example.meiqiweather.R

class Resource {

    companion object{

        private val insideHashNap  by lazy { HashMap<String,Int>() }

        val hashMap by lazy {
            for (i in 0 until image.size)
                insideHashNap[key[i]] = image[i]
            insideHashNap
        }

        private val image by lazy {  arrayOf(
            R.drawable.situation_100, R.drawable.situation_100n, R.drawable.situation_101,
            R.drawable.situation_102, R.drawable.situation_103, R.drawable.situation_103n, R.drawable.situation_104,
            R.drawable.situation_104n, R.drawable.situation_300, R.drawable.situation_300n, R.drawable.situation_301,
            R.drawable.situation_301n, R.drawable.situation_302, R.drawable.situation_303, R.drawable.situation_304,
            R.drawable.situation_305, R.drawable.situation_306, R.drawable.situation_307, R.drawable.situation_309,
            R.drawable.situation_310, R.drawable.situation_311, R.drawable.situation_312, R.drawable.situation_313,
            R.drawable.situation_314, R.drawable.situation_315, R.drawable.situation_316, R.drawable.situation_317,
            R.drawable.situation_318, R.drawable.situation_399, R.drawable.situation_400, R.drawable.situation_401,
            R.drawable.situation_402, R.drawable.situation_403, R.drawable.situation_404, R.drawable.situation_405,
            R.drawable.situation_406, R.drawable.situation_406n, R.drawable.situation_407, R.drawable.situation_407n,
            R.drawable.situation_408, R.drawable.situation_409, R.drawable.situation_410, R.drawable.situation_499,
            R.drawable.situation_500, R.drawable.situation_501, R.drawable.situation_502, R.drawable.situation_503,
            R.drawable.situation_504, R.drawable.situation_507, R.drawable.situation_508, R.drawable.situation_509,
            R.drawable.situation_510, R.drawable.situation_511, R.drawable.situation_512, R.drawable.situation_513,
            R.drawable.situation_514, R.drawable.situation_515) }

        private val key  by lazy { arrayOf("100","100n","101","102","103","103n","104","104n","300","300n","301",
            "301n","302","303","304","305","306","307","309","310","311","312","313","314","315","316","317",
            "318","399","400","401","402","403","404","405","406","406n","407","407n","408","409","410","499",
            "500","501","502","503","504","507","508","509","510","511","512","513","514","515") }

        val lifeData by lazy { hashMapOf(
            "comf" to Data("舒适度指数", R.drawable.ic_comf, R.drawable.comf),
            "drsg" to Data("穿衣指数", R.drawable.ic_drsg, R.drawable.drsg),
            "flu" to Data("感冒指数", R.drawable.ic_flu, R.drawable.flu),
            "sport" to Data("运动指数", R.drawable.ic_sport, R.drawable.sport),
            "trav" to Data("旅游指数", R.drawable.ic_trav, R.drawable.trav),
            "uv" to Data("紫外线指数", R.drawable.ic_uv, R.drawable.uv),
            "cw" to Data("洗车指数", R.drawable.ic_cw, R.drawable.cw),
            "air" to Data("空气污染扩散条件指数", R.drawable.ic_air, R.drawable.air)
        ) }

        fun getStatusBarHeight(activity: Activity): Int {
            val resources = activity.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            val height = resources.getDimensionPixelSize(resourceId)
            return height
        }

    }

}