package com.example.qp

import java.io.Serializable

data class Question(
    var time: String? = "",
    var title:String?="",
    var content: String? = "",
    var tag1: String? = "",
    var tag2: String? = "",
    var tag3: String? = "",
    var answerList:ArrayList<Answer>? = null
) : Serializable
