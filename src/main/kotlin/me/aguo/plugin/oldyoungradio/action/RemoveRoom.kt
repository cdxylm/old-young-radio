package me.aguo.plugin.oldyoungradio.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.aguo.plugin.oldyoungradio.SELECTED_ROOM
import me.aguo.plugin.oldyoungradio.TOOL_WINDOW_ROOMS
import me.aguo.plugin.oldyoungradio.service.RoomsService

class RemoveRoom : AnAction(
    "Remove Room",
    "Remove a room from list",
    AllIcons.General.Remove
) {
    override fun actionPerformed(e: AnActionEvent) {
        RoomsService.instance.remove(SELECTED_ROOM)
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        presentation.isEnabled = SELECTED_ROOM.room_id != -99 && !TOOL_WINDOW_ROOMS.isEmpty
        super.update(e)
    }
}