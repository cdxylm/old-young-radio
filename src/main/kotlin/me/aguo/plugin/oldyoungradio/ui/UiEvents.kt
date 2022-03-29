package me.aguo.plugin.oldyoungradio.ui

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import me.aguo.plugin.oldyoungradio.CURRENT_STREAM_URLS
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import me.aguo.plugin.oldyoungradio.service.PlayerService
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JList

@Deprecated("Don't need.")
class CustomMouseListener : MouseListener {
    override fun mouseClicked(p0: MouseEvent?) {
        if (p0?.clickCount == 2) {
            println("Double clicked!")
        }
    }

    override fun mousePressed(p0: MouseEvent?) {
        println("Pressed!")
    }

    override fun mouseReleased(p0: MouseEvent?) {
        println("mouseReleased!")

    }

    override fun mouseEntered(p0: MouseEvent?) {
        println("mouseEntered!")

    }

    override fun mouseExited(p0: MouseEvent?) {
        println("mouseExited!")

    }

}


class CustomMouseAdapter : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent?) {
        val list = e?.source as JList<*>
        if (list.isSelectionEmpty) {
            return
        }
        if (e.clickCount == 1) {
            val roomId = (list.selectedValue as RoomModel).room_id
            CURRENT_STREAM_URLS = if ((list.selectedValue as RoomModel).live_status == 1) {
                val urls = BiliBiliApi.getSteamUrls(roomId)
                urls
            } else {
                listOf()
            }
        }
        if (e.clickCount == 2) {
            if ((list.selectedValue as RoomModel).live_status == 2) {
                @Suppress("DialogTitleCapitalization")
                val notification = Notification(
                    "Old Young Radio",
                    "Old Young Radio",
                    "该用户未开播",
                    NotificationType.WARNING
                )
                Notifications.Bus.notify(notification)
            }
            if (CURRENT_STREAM_URLS.isNotEmpty()) {
                PLAYING_ROOM = (list.selectedValue as RoomModel)
                PlayerService.instance.playVlc(CURRENT_STREAM_URLS)
            }
        }
        super.mouseClicked(e)
    }

}
