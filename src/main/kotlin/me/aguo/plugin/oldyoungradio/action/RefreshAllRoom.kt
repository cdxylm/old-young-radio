package me.aguo.plugin.oldyoungradio.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.aguo.plugin.oldyoungradio.SELECTED_ROOM
import me.aguo.plugin.oldyoungradio.TOOL_WINDOW_ROOMS
import me.aguo.plugin.oldyoungradio.getAllIds
import me.aguo.plugin.oldyoungradio.initSelectedRoom
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi


class RefreshAllRoom : AnAction(
    "Refresh Rooms' Status",
    "Refresh the status of all rooms",
    AllIcons.Actions.Refresh
) {
    override fun actionPerformed(e: AnActionEvent) {
        @Suppress("UNCHECKED_CAST")
        val rooms = BiliBiliApi.getStatusInfoByUids(getAllIds("uid") as List<Int>)
        if (rooms.isNotEmpty()) {
            TOOL_WINDOW_ROOMS.removeAll()
            rooms.sortedBy {
                it?.uid
            }.map {
                TOOL_WINDOW_ROOMS.add(it)
            }
            SELECTED_ROOM = initSelectedRoom()
        }
    }
}