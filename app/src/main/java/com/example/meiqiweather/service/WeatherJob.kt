package com.example.meiqiweather.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class WeatherJob : JobService() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("kkgp", "Service created")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("kkgp", "Service destroyed")
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("kkgp", "onStopJob")
        val builder =  JobInfo.Builder(1, ComponentName(this, WeatherJob::class.java))
        val jobScheduler = this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        builder.setPeriodic(1000 * 60*15)//设置延迟调度时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)//设置所需网络类型
        builder.setRequiresCharging(true)//设置在充电时执行Job
        builder.setRequiresDeviceIdle(false)
        jobScheduler.schedule(builder.build())
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("kkgp", "onStartJob")
        jobFinished(params,false)
        return false
    }


}
