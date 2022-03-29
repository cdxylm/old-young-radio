package me.aguo.plugin.oldyoungradio.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.AnActionButton
import com.intellij.ui.AnActionButtonRunnable
import me.aguo.plugin.oldyoungradio.checkRoomExist
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import me.aguo.plugin.oldyoungradio.service.RoomsService
import me.aguo.plugin.oldyoungradio.ui.CustomDialog

class AddRoom : AnAction("Add Room", "Add a new room to list", AllIcons.General.Add), AnActionButtonRunnable {
    override fun actionPerformed(e: AnActionEvent) {
        val inputDialog = CustomDialog()
        val persistState = RoomsService.instance.state
        inputDialog.showAndGet()
        val inputString = inputDialog.model.inputString
        if (inputString.isNotEmpty()) {
            val pattern = "[,，\n]".toRegex()
            val roomsId = pattern.split(inputString).filter { it.isNotEmpty() }.map { it.toInt() }
            for (i in roomsId) {
                val room = e.project?.let { BiliBiliApi.getRoomInitInfo(i, it) }
                if (room != null && !checkRoomExist(persistState, room.uid)) {
                    RoomsService.instance.add(RoomModel(room.room_id, room.short_id, room.uid))
                }
            }
        }
        RefreshAllRoom().actionPerformed(e)
    }


    override fun run(t: AnActionButton?) {
        TODO("Not yet implemented")
    }
}

