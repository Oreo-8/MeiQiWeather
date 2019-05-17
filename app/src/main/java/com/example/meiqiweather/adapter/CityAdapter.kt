package com.example.meiqiweather.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meiqiweather.R
import com.example.meiqiweather.data.FragmentWeatherData
import kotlinx.android.synthetic.main.city_item.view.*

class CityAdapter(private val mData: ArrayList<FragmentWeatherData>): RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.city_item , p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var data = mData[p1]
        p0.text_1.text = data.city
        p0.text_2.text = data.happening
        p0.text_3.text = data.tmp
        p0.itemView.setOnClickListener{
            itemClickListener?.onItemClickListener(p1)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val text_1 = view.text_1
        val text_2 = view.text_2
        val text_3 = view.text_3
    }

    interface ItemClickListener{
        fun onItemClickListener(position: Int)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}