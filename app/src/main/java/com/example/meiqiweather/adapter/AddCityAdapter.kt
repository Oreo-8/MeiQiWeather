package com.example.meiqiweather.adapter

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meiqiweather.R
import com.example.meiqiweather.data.Resource
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic
import kotlinx.android.synthetic.main.addcity_item.view.*
import kotlinx.android.synthetic.main.city_item.view.*

class AddCityAdapter(private val data: List<Basic>): RecyclerView.Adapter<AddCityAdapter.ViewHolder>() {

    private var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.addcity_item , p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val mdata = data[p1]
        p0.location.text = mdata.location
        p0.parentCity.text = mdata.parent_city
        p0.adminArea.text = mdata.admin_area
        p0.cnty.text = mdata.cnty
        p0.itemView.setOnClickListener{
            itemClickListener?.onItemClickListener(mdata)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val location = view.text_location
        val parentCity = view.text_Parent_city
        val adminArea = view.text_Admin_area
        val cnty = view.text_Cnty
    }

    interface ItemClickListener{
        fun onItemClickListener(basic: Basic)
    }

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

}