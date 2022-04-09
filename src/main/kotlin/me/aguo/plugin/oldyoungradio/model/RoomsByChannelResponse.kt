package me.aguo.plugin.oldyoungradio.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomsByChannelResponse(
    val code: Int,
    val message: Int,
    val ttl: Int,
    val data: RBCRData
)

@JsonClass(generateAdapter = true)
data class RBCRData(

    val banner: List<Banner>,
    val new_tags: List<NewTags>,
    val list: List<RBCRRoomInfo>,
    val count: Int,
    val has_more: Int
)

@JsonClass(generateAdapter = true)
data class RBCRRoomInfo(

    val roomid: Int,
    val uid: Int,
    val title: String,
    val uname: String,
    val online: Int,
    val user_cover: String,
    val user_cover_flag: Int,
    val system_cover: String,
    val cover: String,
    val show_cover: String,
    val link: String,
    val face: String,
    val parent_id: Int,
    val parent_name: String,
    val area_id: Int,
    val area_name: String,
    val area_v2_parent_id: Int,
    val area_v2_parent_name: String,
    val area_v2_id: Int,
    val area_v2_name: String,
    val session_id: String,
    val group_id: Int,
    val show_callback: String,
    val click_callback: String,
    val web_pendent: String,
    val pk_id: Int,
//    val pendant_info : Pendant_info,
    val verify: Verify,
    val head_box: HeadBox?,
    val head_box_type: Int,
    val is_auto_play: Int,
    val flag: Int,
    val watched_show: WatchedShow,
    val is_nft: Int,
    val nft_dmark: String
)

@JsonClass(generateAdapter = true)
data class Banner(

    val id: Int,
    val title: String,
    val location: String,
    val position: Int,
    val pic: String,
    val link: String,
    val weight: Int,
    val room_id: Int,
    val up_id: Int,
    val parent_area_id: Int,
    val area_id: Int,
    val live_status: Int,
    val av_id: Int,
    val is_ad: Boolean,
//    val ad_transparent_content: String,
    val show_ad_icon: Boolean
)

@JsonClass(generateAdapter = true)
data class NewTags(

    val id: Int,
    val name: String,
    val icon: String,
    val sort_type: String,
    val type: Int,
    val sub: List<String>,
    val hero_list: List<String>,
    val sort: Int
)

@JsonClass(generateAdapter = true)
data class WatchedShow(

    val switch: Boolean,
    val num: Int,
//    val text_small: Int,
    val text_large: String,
    val icon: String,
    val icon_location: Int,
    val icon_web: String
)

@JsonClass(generateAdapter = true)
data class Verify(

    val role: Int,
    val desc: String,
    val type: Int
)

@JsonClass(generateAdapter = true)
data class HeadBox(

    val name: String,
    val value: String,
    val desc: String
)
