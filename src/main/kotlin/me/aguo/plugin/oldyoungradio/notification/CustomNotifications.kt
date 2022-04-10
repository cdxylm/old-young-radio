@file:Suppress("DialogTitleCapitalization")

package me.aguo.plugin.oldyoungradio.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

object CustomNotifications {
    fun apiError() {
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "Api返回错误",
            NotificationType.WARNING
        )
        Notifications.Bus.notify(notification)
    }

    fun offline(){
        val notification = Notification(
            "Old Young Radio",
            "Old Young Radio",
            "该用户未开播",
            NotificationType.WARNING
        )
        Notifications.Bus.notify(notification)
    }
}