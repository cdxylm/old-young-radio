package me.aguo.plugin.oldyoungradio.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.project.Project
import com.squareup.moshi.Moshi
import me.aguo.plugin.oldyoungradio.getPersistModel
import me.aguo.plugin.oldyoungradio.model.*
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

    fun getRoomInitInfo(room_id: Int, project: Project): RoomInitInfo? {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.live.bilibili.com/room/v1/Room/room_init?id=${room_id}"))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.body().indexOf("message\":\"ok") != -1) {
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(RoomInitInfoResponse::class.java)
            val json = jsonAdapter.fromJson(response.body())
            return json!!.data
        }
        notifyError(project, "message: " + response.body().substringBefore(",\"data").substringAfter("message\":"))
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

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.live.bilibili.com/room/v1/Room/get_status_info_by_uids"))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().indexOf("message\":\"success") != -1 &&
            response.body().indexOf("\"data\":[]") == -1
        ) {
            val moshi = Moshi.Builder().build()
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
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
//            .uri(URI.create("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=${room_id}"))
            .uri(URI.create("https://api.live.bilibili.com/xlive/web-room/v2/index/getRoomPlayInfo?room_id=${room_id}&no_playurl=0&mask=1&platform=web&protocol=0,1&format=0,1,2&codec=0,1"))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().indexOf("message\":\"0") != -1) {
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(StreamUrlResponseV2::class.java)

            val streamUrlsResponseV2 = jsonAdapter.fromJson(response.body())
            return parseUrls(streamUrlsResponseV2)
        }
        return mutableListOf()
    }
}