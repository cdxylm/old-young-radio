package me.aguo.plugin.oldyoungradio.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.service.PlayerService
import me.aguo.plugin.oldyoungradio.ui.PluginIcons

class StopRadio : AnAction(
    "Stop",
    "Stop",
    PluginIcons.stopIcon
) {
    override fun actionPerformed(e: AnActionEvent) {
        PlayerService.instance.stopVlc()
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        presentation.text = "Stop: " + PLAYING_ROOM.title
        presentation.description = PLAYING_ROOM.title.ifEmpty { "Stop" }
        presentation.isEnabled = PLAYING_ROOM.room_id != -99
        super.update(e)
    }
}