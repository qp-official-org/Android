package com.example.qp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Question(
    var time: String? = "",
    @SerializedName(value="title")var title:String?="",
    @SerializedName(value="content")var content: String? = "",
//    var tag1: String? = "",
//    var tag2: String? = "",
//    var tag3: String? = "",
    @SerializedName(value="hashtag") var hashtags:ArrayList<Hashtag> =ArrayList<Hashtag>(),
    var answerList:ArrayList<Answer>? = null,
    @SerializedName(value="userId")var userId:Int=0
) : Serializable
