package me.aguo.plugin.oldyoungradio.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StreamUrlResponseV2(
    val code: Int,
    val message: Int,
    val ttl: Int,
    val data: Data
)

@JsonClass(generateAdapter = true)
data class Codec(
    val codec_name: String,
    val current_qn: Int,
    val accept_qn: List<Int>,
    val base_url: String,
    val url_info: List<UrlInfo>,
//     val hdr_qn: String,
    val dolby_type: Int
)

@JsonClass(generateAdapter = true)
data class Data(
    val room_id: Int,
    val short_id: Int,
    val uid: Int,
    val is_hidden: Boolean,
    val is_locked: Boolean,
    val is_portrait: Boolean,
    val live_status: Int,
    val hidden_till: Int,
    val lock_till: Int,
    val encrypted: Boolean,
    val pwd_verified: Boolean,
    val live_time: Int,
    val room_shield: Int,
//     val all_special_types: List<String>,
    val playurl_info: PlayUrlInfo
)

@JsonClass(generateAdapter = true)
data class Format(
    val format_name: String,
    val codec: List<Codec>
)

@JsonClass(generateAdapter = true)
data class GQNDesc(
    val qn: Int,
    val desc: String,
    val hdr_desc: String
)

@JsonClass(generateAdapter = true)
data class P2PData(
    val p2p: Boolean,
    val p2p_type: Int,
    val m_p2p: Boolean,
//     val m_servers: String
)

@JsonClass(generateAdapter = true)
data class PlayUrl(
    val cid: Int,
    val g_qn_desc: List<GQNDesc>,
    val stream: List<Stream>,
    val p2p_data: P2PData,
//     val dolby_qn: String
)

@JsonClass(generateAdapter = true)
data class PlayUrlInfo(
//     val conf_json: String,
    val playurl: PlayUrl
)

@JsonClass(generateAdapter = true)
data class Stream(
    val protocol_name: String,
    val format: List<Format>
)

@JsonClass(generateAdapter = true)
data class UrlInfo(
    val host: String,
    val extra: String,
    val stream_ttl: Int
)