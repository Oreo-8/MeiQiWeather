package com.example.meiqiweather.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import java.util.*

class ViewPagerAdapter(fm: FragmentManager, private var marrayList: ArrayList<Fragment>) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = marrayList.size

    override fun getItem(position: Int): Fragment = marrayList[position]

//    override fun getItemId(position: Int): Long = marrayList[position].hashCode().toLong()

    override fun getItemPosition(obj: Any): Int = POSITION_NONE

}