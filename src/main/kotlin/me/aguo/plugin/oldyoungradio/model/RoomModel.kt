package me.aguo.plugin.oldyoungradio.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomModel(
    val room_id: Int,
    val short_id: Int,
    val uid: Int,
    var uname: String = "",
    var title: String = "",
    var live_status: Int = 2
)