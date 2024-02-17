package com.example.qp

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    companion object{
        lateinit var preferences: PreferenceUtil
    }

    override fun onCreate() {
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
        preferences = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}