package me.aguo.plugin.oldyoungradio.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.squareup.moshi.Moshi
import me.aguo.plugin.oldyoungradio.Pattern
import me.aguo.plugin.oldyoungradio.getPersistModel
import me.aguo.plugin.oldyoungradio.model.*
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import me.aguo.plugin.oldyoungradio.notifyError
import me.aguo.plugin.oldyoungradio.parseUrls
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/*
参见Github上B站API项目 https://github.com/SocialSisterYi/bilibili-API-collect/
 */

object BiliBiliApi {
    private val client: HttpClient = HttpClient.newBuilder().build()
    private val requestBuilder = HttpRequest.newBuilder()
    private val moshi = Moshi.Builder().build()


    fun getRoomInitInfo(room_id: Int, project: Project): RoomInitInfo? {
        val request = requestBuilder
            .uri(URI.create("https://api.live.bilibili.com/room/v1/Room/room_init?id=${room_id}"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.body().indexOf("message\":\"ok") != -1) {
            val jsonAdapter = moshi.adapter(RoomInitInfoResponse::class.java)
            val json = jsonAdapter.fromJson(response.body())
            return json!!.data
        }
        notifyError(project, "message: " + response.body().substringBefore(",\"data").substringAfter("message\":"))
        return null
    }

    fun getRoomInfoByMid(mid: Int): RoomModel? {
        val request = requestBuilder
            .uri(URI.create("https://api.bilibili.com/x/space/acc/info?mid=${mid}"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().indexOf("code\":0") != -1) {
            val liveRoom = Pattern.live_rooms.toRegex().find(response.body())
            liveRoom?.let {
                val liveRoomInfo = Pattern.live_rooms_info.toRegex().find(it.groupValues[3])
                return liveRoomInfo?.let {
                    RoomModel(
                        room_id = liveRoomInfo.groupValues[4].toInt(),
                        short_id = 0,
                        uid = mid,
//                        uname = liveRoom.groupValues[2],
//                        title = liveRoomInfo.groupValues[3],
//                        live_status = liveRoomInfo.groupValues[2].toInt()
                    )
                }
            }
        }
        return null
    }

    fun getStatusInfoByUids(uids: List<Int>): List<RoomModel?> {
        if (uids.isEmpty()) {
            return getPersistModel()
        }
        val values = mapOf("uids" to uids)

        val objectMapper = ObjectMapper()
        val requestBody: String = objectMapper
            .writeValueAsString(values)

        val request = requestBuilder
            .uri(URI.create("https://api.live.bilibili.com/room/v1/Room/get_status_info_by_uids"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().indexOf("message\":\"success") != -1 &&
            response.body().indexOf("\"data\":[]") == -1
        ) {
            val jsonAdapter = moshi.adapter(StatusByUidsResponse::class.java)
            val statusByUids = jsonAdapter.fromJson(response.body())
            val rooms = statusByUids!!.data.map {
                RoomModel(
                    it.value.room_id,
                    it.value.short_id,
                    it.value.uid,
                    it.value.uname,
                    it.value.title,
                    it.value.live_status,
                )
            }
            return rooms
        }
        return getPersistModel()
    }

    fun getSteamUrls(room_id: Int): MutableList<String> {
        val request = requestBuilder
            .uri(URI.create("https://api.live.bilibili.com/xlive/web-room/v2/index/getRoomPlayInfo?room_id=${room_id}&no_playurl=0&mask=1&platform=web&protocol=0,1&format=0&codec=0,1"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val apiResponseCorrect = response.body().indexOf("message\":\"0") != -1
        val onlineStatus = response.body().indexOf("live_status\":1") != -1
        if (apiResponseCorrect && onlineStatus) {
            //Sometimes the status in panel doesn't update in time
            //When the room is offline, the api response->message is also "0" and playurl_info is null, so need check it status again.
            val jsonAdapter = moshi.adapter(StreamUrlResponseV2::class.java)
            val streamUrlsResponseV2 = jsonAdapter.fromJson(response.body())
            return parseUrls(streamUrlsResponseV2)
        }
        if (!apiResponseCorrect) CustomNotifications.apiError() else CustomNotifications.offline()
        return mutableListOf()
    }

    fun getFollowings(mid: Int): List<Int>? {
        val request = requestBuilder
            .uri(URI.create("https://api.bilibili.com/x/relation/followings?vmid=${mid}&pn=1&ps=50&order=desc&order_type=attention"))
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().indexOf("code\":0") != -1) {
            val jsonAdapter = moshi.adapter(Followings::class.java)
            val followings = jsonAdapter.fromJson(response.body())
            val midList = followings?.data?.list?.map {
                it.mid
            }
            if (followings?.data?.total!! > 50) {
                @Suppress("DialogTitleCapitalization")
                Messages.showInfoMessage("你关注的实在是太多了，罢工了......", "你总共关注了${followings.data.total}个用户！")
            }
            return midList
        }
        return null
    }

    fun getRoomsByChannel(parentAreaId: Int): List<RBCRRoomInfo>? {
        val request = requestBuilder
            .uri(URI.create("https://api.live.bilibili.com/xlive/web-interface/v1/second/getList?platform=web&parent_area_id=${parentAreaId}&area_id=0&page=1"))
            .GET().build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().indexOf("message\":\"0") != -1) {
            val jsonAdapter = moshi.adapter(RoomsByChannelResponse::class.java)
            val roomsByChannelResponse = jsonAdapter.fromJson(response.body())
            return roomsByChannelResponse?.data?.list
        }
        return null
    }
}