package com.example.myapplication

data class RoomData(
    val seed : String,
    val title : String,
    val master : String,
    var memberCnt : Int,
    val memberList: MutableList<String>? = null
)
