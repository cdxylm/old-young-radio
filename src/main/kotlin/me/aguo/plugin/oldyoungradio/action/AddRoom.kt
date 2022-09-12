package me.aguo.plugin.oldyoungradio.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.aguo.plugin.oldyoungradio.Pattern
import me.aguo.plugin.oldyoungradio.checkRoomExist
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi.getFollowings
import me.aguo.plugin.oldyoungradio.service.RoomsService
import me.aguo.plugin.oldyoungradio.ui.CustomDialog

class AddRoom : AnAction("Add Room", "Add a new room to list", AllIcons.General.Add) {
    override fun actionPerformed(e: AnActionEvent) {
        val inputDialog = CustomDialog()
        val persistState = RoomsService.instance.state
        inputDialog.showAndGet()
        val inputString = inputDialog.model.inputString
        if (inputString.isNotEmpty()) {
            val rooms = mutableListOf<RoomModel>()
            val followingsPattern = Pattern.followings.toRegex()
            val result = followingsPattern.find(inputString)
            if (result != null) {
                val followings = getFollowings(result.groupValues[1].toLong())
                followings?.map {
                    val room = BiliBiliApi.getRoomInfoByMid(it)
                    room?.let { r -> rooms.add(r) }
                }
            } else {
                val tempString = Pattern.notRoomId.toRegex().replace(inputString, "")
                val separatorPattern = Pattern.separator.toRegex()
                val roomsId = separatorPattern.split(tempString).filter { it.isNotEmpty() }.map { it.toInt() }
                for (i in roomsId) {
                    val room = e.project?.let { BiliBiliApi.getRoomInitInfo(i, it) }
                    room?.let { rooms.add(RoomModel(it.room_id, it.short_id, it.uid)) }
                }
            }
            rooms.forEach {
                if (!checkRoomExist(persistState, it.uid)) {
                    RoomsService.instance.add(RoomModel(it.room_id, it.short_id, it.uid))
                }
            }
        }
        RefreshAllRoom().actionPerformed(e)
    }
}

