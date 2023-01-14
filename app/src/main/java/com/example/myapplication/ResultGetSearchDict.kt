package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class ResultGetSearchDict(
    var lastBuildDate: String = "",
    var total: Int = 0,
    var start: Int = 0,
    var display: Int = 0,
    var items: List<Items>
)

data class Items(
    @SerializedName("title")
    var name: String = "", // title(origin) > name
    //var originallink: String = "",
    var link: String = "",
    var description: String = "",
    //var pubDate: String = ""
    var thumbnail: String = ""
)