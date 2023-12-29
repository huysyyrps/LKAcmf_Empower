package com.example.lkacmf_empower

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent


class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}