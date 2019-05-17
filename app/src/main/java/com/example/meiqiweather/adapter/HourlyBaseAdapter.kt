package com.example.meiqiweather.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.meiqiweather.R
import com.example.meiqiweather.data.Hourly
import interfaces.heweather.com.interfacesmodule.bean.weather.hourly.HourlyBase
import kotlinx.android.synthetic.main.hour_item.view.*

class HourlyBaseAdapter(private val data: List<Hourly>, private val hashMap: HashMap<String,Int>):
    RecyclerView.Adapter<HourlyBaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(p0.context).inflate(R.layout.hour_item , p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(vh: ViewHolder, p1: Int) {
        var hourlyBase = data[p1]
        hashMap[hourlyBase.cond_code]?.let { vh.image.setImageResource(it) }
        vh.text1.text = hourlyBase.time.substring(11)
        vh.text2.text = hourlyBase.tmp + "°"
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.item_image
        val text1: TextView = view.item_text1
        val text2: TextView = view.item_text2
    }

}