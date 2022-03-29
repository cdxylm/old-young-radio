package me.aguo.plugin.oldyoungradio

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.squareup.moshi.Moshi
import me.aguo.plugin.oldyoungradio.model.Codec
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.model.StreamUrlResponseV2
import me.aguo.plugin.oldyoungradio.model.UrlInfo
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
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

fun execCommand(cmd: Array<String>) {
    val ps = Runtime.getRuntime().exec(cmd)
    println(ps.pid())
}

@Deprecated(
    "Don't need execute command to play stream",
    ReplaceWith("Player.instance.playInVlcJ()", "Utils.playInVlcJ"),
    DeprecationLevel.ERROR
)
fun play(room: Int, cmd: String) {
    val urls = BiliBiliApi.getSteamUrls(room)
    CURRENT_STREAM_URLS = urls
    if (CURRENT_STREAM_URLS.isNotEmpty()) {
        val firstUrl = urls[0]
        val currentCmd = arrayOf(cmd, "-I dummy", "--no-video", firstUrl)
        execCommand(currentCmd)
    }
}


fun initSelectedRoom(): RoomModel {
    return RoomModel(-99, -99, -99)
}


fun parseUrls(json: StreamUrlResponseV2?): MutableList<String> {
    if (json == null) {
        return mutableListOf()
    }
    val flvStreamUrl = mutableListOf<String>()

    val stream = json.data.playurl_info.playurl.stream
    val flvStream: Codec = stream[0].format[0].codec[0]
    val baseUrl = flvStream.base_url
    val urlInfo: List<UrlInfo> = flvStream.url_info

    for (i in urlInfo) {
        flvStreamUrl.add(i.host + baseUrl + i.extra)
    }
    return flvStreamUrl
}

fun notifyError(project: Project?, content: String?) {
    @Suppress("DialogTitleCapitalization")
    NotificationGroupManager.getInstance().getNotificationGroup("Old Young Radio")
        .createNotification("添加房间错误", content!!, NotificationType.ERROR)
        .notify(project)
}