@file:Suppress("DialogTitleCapitalization")

package me.aguo.plugin.oldyoungradio.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import me.aguo.plugin.oldyoungradio.action.ConfigureVlcDir

object CustomNotifications {

    fun noVlc() {
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "插件没有找到VLC，请尝试手动配置Vlc目录<br>(包含libvlc、libvlccore等文件，可能需要重启应用)",
            NotificationType.ERROR,
        )
        notification.addAction(ConfigureVlcDir())
        Notifications.Bus.notify(notification)
    }

    fun restartApp() {
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "插件配置已修改，请重启IDE或应用。",
            NotificationType.WARNING,
        )
        Notifications.Bus.notify(notification)
    }

    fun apiError() {
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "Api返回错误",
            NotificationType.WARNING
        )
        Notifications.Bus.notify(notification)
    }

    fun offline() {
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "该用户未开播",
            NotificationType.WARNING
        )
        Notifications.Bus.notify(notification)
    }
}