package com.example.buggubuggu

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // KakaoMapSdk 초기화
        KakaoMapSdk.init(this, "0afb129ab46b04f0675705074d21303e")
    }
}