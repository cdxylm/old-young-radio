package me.aguo.plugin.oldyoungradio.ui

import com.intellij.codeInsight.hints.presentation.MouseButton
import com.intellij.codeInsight.hints.presentation.mouseButton
import com.intellij.ide.DataManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.JBPopupMenu
import me.aguo.plugin.oldyoungradio.CURRENT_STREAM_URLS
import me.aguo.plugin.oldyoungradio.action.RefreshAllRoom
import me.aguo.plugin.oldyoungradio.checkRoomExist
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import me.aguo.plugin.oldyoungradio.service.PlayerService
import me.aguo.plugin.oldyoungradio.service.RoomsService
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JList
import javax.swing.JMenuItem


class CustomMouseAdapter(private val tabId: Int) : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent?) {
        val list = e?.source as JList<*>
        val validArea = list.indexToLocation(list.lastVisibleIndex) ?: return
        val cellHeight = list.getCellBounds(0, 1).height
        validArea.y += cellHeight
        if (!(validArea.x <= e.point.x && validArea.y >= e.point.y)) {
            // 默认的左键点击行为里，点击的位置不在列表的项里也会自动选中最后一项，所以这里其实可以不做位置判断
            return
        }
        if (tabId == 2 && e.mouseButton == MouseButton.Right) {
            // 仅让channel面板响应右键事件
            val index = list.locationToIndex(e.point)
            list.selectedIndex = index
            val popupMenu = JBPopupMenu()
            val subscribe = JMenuItem("订阅")
            popupMenu.add(subscribe)
            subscribe.addActionListener {
                val project = DataManager.getInstance().getDataContext(list).getData(CommonDataKeys.PROJECT)!!
                val room = BiliBiliApi.getRoomInitInfo((list.selectedValue as RoomModel).room_id, project)
                if (room != null) {
                    if (!checkRoomExist(RoomsService.instance.state, room.uid)) {
                        RoomsService.instance.add(RoomModel(room.room_id, room.short_id, room.uid))
                        RefreshAllRoom().actionPerformed(
                            AnActionEvent(
                                null, DataManager.getInstance().getDataContext(e.component),
                                ActionPlaces.TOOLWINDOW_POPUP, Presentation("Subscribe"), ActionManager.getInstance(), 0
                            )
                        )
                    }
                }
            }
            popupMenu.show(list, e.x, e.y)
        }
        if (e.clickCount == 2 && e.mouseButton == MouseButton.Left) {
            if ((list.selectedValue as RoomModel).live_status != 1) {
                @Suppress("DialogTitleCapitalization")
                val notification = Notification(
                    "Old Young Radio",
                    "Old Young Radio",
                    "该用户未开播",
                    NotificationType.WARNING
                )
                Notifications.Bus.notify(notification)
                return
            }
            val roomId = (list.selectedValue as RoomModel).room_id
            CURRENT_STREAM_URLS = if ((list.selectedValue as RoomModel).live_status == 1) {
                val urls = BiliBiliApi.getSteamUrls(roomId)
                urls
            } else {
                listOf()
            }
            if (CURRENT_STREAM_URLS.isNotEmpty()) {
                PlayerService.instance.playVlc(CURRENT_STREAM_URLS, list.selectedValue as RoomModel)
            }
        }

        super.mouseClicked(e)
    }
}
