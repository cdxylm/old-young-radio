package me.aguo.plugin.oldyoungradio.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.ColoredListCellRenderer
import me.aguo.plugin.oldyoungradio.SELECTED_ROOM
import me.aguo.plugin.oldyoungradio.model.RoomModel
import javax.swing.JList


class CustomRenderer : ColoredListCellRenderer<RoomModel>() {
    private val logger = Logger.getInstance(CustomRenderer::class.java)
    override fun customizeCellRenderer(
        list: JList<out RoomModel>,
        value: RoomModel?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        if (value != null) {
            this.append(value.uname)
            this.append("    ")
            this.append(value.title)
            this.iconTextGap = 20
            this.icon = if (value.live_status == 1) PluginIcons.onlineIcon else PluginIcons.offlineIcon
        } else {
            logger.warn("no value")
        }
        if (selected && value != null) {
            SELECTED_ROOM = value
        }
    }

}