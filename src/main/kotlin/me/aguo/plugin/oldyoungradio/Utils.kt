package me.aguo.plugin.oldyoungradio

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.squareup.moshi.Moshi
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.model.StreamUrlResponseV2
import me.aguo.plugin.oldyoungradio.service.RoomsService


fun checkRoomExist(state: RoomsService.State, uid: Int): Boolean {
    val tempList = state.roomList.filter {
        it.indexOf("uid=${uid}") != -1
    }
    return tempList.isNotEmpty()
}

fun getAllIds(type: String): List<Int?> {
    if (type !in listOf("room_id", "short_id", "uid")) {
        throw Exception("id type must be \"room_id\" or \"short_id\",\"uid\"")
    }
    val roomList = RoomsService.instance.state.roomList
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(RoomModel::class.java)
    val ids = roomList.map {
        val room = jsonAdapter.fromJson(it)
        when (type) {
            "room_id" -> room!!.room_id
            "short_id" -> room!!.short_id
            else -> room!!.uid
        }
    }
    return ids
}

fun getPersistModel(): List<RoomModel?> {
    val roomList = RoomsService.instance.state.roomList
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(RoomModel::class.java)
    return roomList.map {
        jsonAdapter.fromJson(it)
    }
}


fun initSelectedRoom(): RoomModel {
    return RoomModel(-99, -99, -99)
}


fun parseUrls(json: StreamUrlResponseV2?): List<String> {
    if (json == null) {
        return listOf()
    }
    val streamList = json.data.playurl_info.playurl.stream
    val formatStreamMap = mutableMapOf<String, List<String>>()
    for (s in streamList) {
        for (f in s.format) {
            val streamUrl = mutableListOf<String>()
            for (c in f.codec) {
                val baseUrl = c.base_url
                val urlInfo = c.url_info
                for (i in urlInfo) {
                    streamUrl.add(i.host + baseUrl + i.extra)
                }
            }
            formatStreamMap[f.format_name] = streamUrl
        }
    }
    val mode = RoomsService.instance.state.settings["format"].toString()
    return formatStreamMap[mode] ?: listOf()
}

fun notifyError(project: Project?, content: String?) {
    @Suppress("DialogTitleCapitalization")
    NotificationGroupManager.getInstance().getNotificationGroup("Old Young Radio")
        .createNotification("添加房间错误", content!!, NotificationType.ERROR)
        .notify(project)
}