package com.example.nmnm

import android.app.Application
import com.example.nmnm.cach.TokenManager

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        TokenManager.init(this)
    }
}