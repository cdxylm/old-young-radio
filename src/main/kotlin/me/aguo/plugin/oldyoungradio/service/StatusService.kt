package me.aguo.plugin.oldyoungradio.service

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.EdtExecutorService
import me.aguo.plugin.oldyoungradio.TOOL_WINDOW_ROOMS
import me.aguo.plugin.oldyoungradio.getAllIds
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class StatusService : Disposable {
    private var statusFuture: ScheduledFuture<*>? = null
    private val logger = Logger.getInstance(StatusService::class.java)

    init {
        Disposer.register(ApplicationManager.getApplication(), this)
    }

    companion object {
        val instance by lazy {
            StatusService()
        }
    }


    private fun getStatus() {
//        logger.warn("trying refresh")
        @Suppress("UNCHECKED_CAST")
        var tempRooms = BiliBiliApi.getStatusInfoByUids(getAllIds("uid") as List<Int>)
        if (tempRooms.toSet() != TOOL_WINDOW_ROOMS.toList().toSet()) {
            logger.info("Rooms' status changed.")

            val lastOffline = TOOL_WINDOW_ROOMS.toList().filter { it.live_status != 1 }
                .toSet()

            val currentOnline = tempRooms.filter { it?.live_status == 1 }
                .toSet()
            val newOnline = currentOnline.filter { online ->
                online?.room_id in lastOffline.map { it.room_id }
            }
            if (newOnline.isNotEmpty()) {
                newOnline.map {
                    if (it != null) {
                        @Suppress("DialogTitleCapitalization")
                        val notification = Notification(
                            "Old Young Radio",
                            it.uname + "开播啦!",
                            it.title,
                            NotificationType.INFORMATION
                        )
                        Notifications.Bus.notify(notification)
                    }
                }
            }
            TOOL_WINDOW_ROOMS.removeAll()
            tempRooms = tempRooms.sortedBy { it?.room_id }
            TOOL_WINDOW_ROOMS.addAll(0, tempRooms)
        }
    }

    fun start() {
        statusFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
            { getStatus() }, 1, 10, TimeUnit.SECONDS
        )
        logger.warn("Status future started.")
    }

    private fun stop() {
        if (statusFuture != null) {
            statusFuture!!.cancel(true)
            statusFuture = null
            logger.warn("Status future cancelled.")
        }
    }

    override fun dispose() {
        stop()
    }
}