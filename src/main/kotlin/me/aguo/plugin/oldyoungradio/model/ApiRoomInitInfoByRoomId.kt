package me.aguo.plugin.oldyoungradio.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomInitInfoResponse(
    val code: Int,
    val msg: String,
    val message: String,
    val data: RoomInitInfo
)


@JsonClass(generateAdapter = true)
data class RoomInitInfo(
    val room_id: Int,
    val short_id: Int,
    val uid: Long,  //B站更新出现16位UID
)


@JsonClass(generateAdapter = true)
data class StatusByUidsResponse(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Map<String, StatusOfRoom>
)


@JsonClass(generateAdapter = true)
data class StatusOfRoom(
    val room_id: Int,
    val short_id: Int,
    val uid: Long,  //B站更新出现16位UID
    val title: String,
    val online: Int,
    val live_status: Int,
    val uname: String
)