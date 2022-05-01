@file:Suppress("DialogTitleCapitalization")

package me.aguo.plugin.oldyoungradio.notification

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.aguo.plugin.oldyoungradio.action.ConfigureStreamFormat
import me.aguo.plugin.oldyoungradio.action.ConfigureVlcDir
import me.aguo.plugin.oldyoungradio.service.PlayerService

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

    fun noUrl(reason: String = "该格式下没有找到直播流地址") {
        val notification = Notification(
            "Old Young Radio",
            "播放遇到错误",
            "$reason，请修改视频流格式（Stream format）后重试。",
            NotificationType.WARNING
        )
        notification.addAction(ConfigureStreamFormat())
        Notifications.Bus.notify(notification)
    }

    fun connectException() {
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "网络请求失败，请检查网络连接。",
            NotificationType.WARNING
        )
        Notifications.Bus.notify(notification)
    }

    fun playerNotReady() {
        val notification = Notification(
            "Old Young Radio",
            "播放器尚未准备完成",
            "由于播放器未能正常停止播放，需要等待播放器进入\"Stopped\"状态。" +
                    "【 可能的原因是正在播放时网络连接意外断开，请在网络恢复后等待播放器恢复正常，或在网络恢复后重启应用 】",
            NotificationType.WARNING
        )
        notification.addAction(object : AnAction("确认网络没有断开过", "", AllIcons.General.BalloonWarning) {
            override fun actionPerformed(e: AnActionEvent) {
                PlayerService.instance.myPlayer = null
                PlayerService.instance.readyPlayNext = true
            }

        })
        Notifications.Bus.notify(notification)
    }

    fun playerReady() {
        val notification = Notification(
            "Old Young Radio",
            "播放器恢复正常",
            "播放器从异常状态中恢复了，现在可以进行下一次播放。",
            NotificationType.INFORMATION
        )
        Notifications.Bus.notify(notification)
    }
}