package me.aguo.plugin.oldyoungradio.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Followings(
    val code: Int,
    val message: Int,
    val ttl: Int,
    val data: FollowingsData
)

@JsonClass(generateAdapter = true)
data class FollowingsData(

    val list: List<FollowingList>,
    val re_version: Int,
    val total: Int
)

@JsonClass(generateAdapter = true)
data class FollowingList(

    val mid: Long,  //B站更新出现16位UID
    val attribute: Int,
    val mtime: Int,
    val tag: List<String>,
    val special: Int,
    val uname: String,
    val face: String,
    val sign: String,
    val official_verify: OfficialVerify,
    val vip: Vip
)

@JsonClass(generateAdapter = true)
data class Label(

    val path: String
)

@JsonClass(generateAdapter = true)
data class OfficialVerify(

    val type: Int,
    val desc: String
)

@JsonClass(generateAdapter = true)
data class Vip(

    val vipType: Int,
    val vipDueDate: Long,
    val dueRemark: String,
    val accessStatus: Int,
    val vipStatus: Int,
    val vipStatusWarn: String,
    val themeType: Int,
    val label: Label
)