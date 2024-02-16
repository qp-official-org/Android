package com.example.qp

import android.app.Application

class AppData : Application() {
    companion object {

        //전역 변수 정의
        var qpAccessToken = ""
        var qpUserID = 0

        var qpNickname = ""
        var qpProfileImage = ""
        var qpEmail = ""
        var qpGender = ""
        var qpPoint = 0

        var searchRecord = ArrayList<String>()
    }
}