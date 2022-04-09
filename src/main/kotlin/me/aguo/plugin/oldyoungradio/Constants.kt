package me.aguo.plugin.oldyoungradio

class Pattern {
    companion object {
        const val separator = "[,，\n]"
        const val notRoomId = "[^\\d，,\n]" // 删除输入字符中的非数字和分隔符 用
        const val live_rooms = "mid\":(\\d+),\"name\":(.*?),.*?\"live_room\":(\\{.*?}),"
        const val live_rooms_info = "roomStatus\":(\\d).*?liveStatus\":(\\d).*?title\":(\".*?\").*?roomid\":(\\d+)"
        const val followings = "https://space\\.bilibili\\.com/(\\d+)"
    }


}

enum class Channels(val title: String, val parentAreaId: Int) {
    CHANNEL_ALL("频道", parentAreaId = 0),
    CHANNEL_WANGYOU("网游", 2),
    CHANNEL_SHOUYOU("手游", 3),
    CHANNEL_DANJI("单机", 6),
    CHANNEL_YULE("娱乐", 1),
    CHANNEL_DIANTAI("电台", 5),
    CHANNEL_XUNI("虚拟", 9),
    CHANNEL_SHENGHUO("生活", 10),
    CHANNEL_XUEXI("学习", 11),
    CHANNEL_SAISHI("赛事", 13)
}