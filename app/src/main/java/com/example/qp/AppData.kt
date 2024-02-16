package com.example.qp

import android.app.Application

class AppData : Application() {
    companion object {
        //전역 변수 정의
        var qpUserData = QpUserData("", 0)
    }
}