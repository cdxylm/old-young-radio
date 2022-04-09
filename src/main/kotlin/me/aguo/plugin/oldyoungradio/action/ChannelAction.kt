package me.aguo.plugin.oldyoungradio.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.aguo.plugin.oldyoungradio.CHANNEL_TAB_ROOMS
import me.aguo.plugin.oldyoungradio.CURRENT_CHANNEL
import me.aguo.plugin.oldyoungradio.Channels
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi

class ChannelAction(val channel: Channels) : AnAction(channel.title) {

    override fun actionPerformed(e: AnActionEvent) {
        val rooms = if (channel.parentAreaId != 0) BiliBiliApi.getRoomsByChannel(channel.parentAreaId) else null
        val tempRooms = rooms?.map {
            RoomModel(it.roomid, 0, it.uid, it.uname, it.title, 1)
        }
        if (tempRooms != null) {
            CHANNEL_TAB_ROOMS.removeAll()
            CHANNEL_TAB_ROOMS.addAll(0, tempRooms)
        }
        if (e.presentation.text != null) {
            CURRENT_CHANNEL = Channels.values().firstOrNull {
                it.title == e.presentation.text
            }!!
        }
    }
}