package me.aguo.plugin.oldyoungradio.service

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import me.aguo.plugin.oldyoungradio.TOOL_WINDOW_ROOMS
import me.aguo.plugin.oldyoungradio.getAllIds
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class StatusService : Disposable {
    private val logger = Logger.getInstance(StatusService::class.java)
    private var errorTask = 0
    internal var statusFuture: ScheduledFuture<*>? = null
    internal var lastRefreshTime: Long? = null


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
        lastRefreshTime = System.currentTimeMillis()
        @Suppress("UNCHECKED_CAST")
        var tempRooms = BiliBiliApi.getStatusInfoByUids(getAllIds("uid") as List<Int>, true)
        lastRefreshTime = System.currentTimeMillis()
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
            tempRooms = tempRooms.sortedBy { it?.uid }
            TOOL_WINDOW_ROOMS.addAll(0, tempRooms)
        }
    }

    fun start() {
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        /*
        如果有任何异常没有被捕捉，schedule任务执行失败一次就会被终止。
        如果任务连续5次执行失败就取消，满足条件后Application listener将会重启任务。
         */
        statusFuture = scheduler.scheduleWithFixedDelay(
            {
                try {
                    getStatus()
                    errorTask = 0
                } catch (t: Throwable) {
                    logger.warn("Timing task failed.(errorTask:$errorTask)")
                    errorTask += 1
                    if (errorTask > 5) {
                        stop()
                    }
                }
            }, 1, 15, TimeUnit.SECONDS
        )
        logger.warn("Status future started.")
    }


    fun restart() {
        stop()
        start()
    }

    private fun stop() {
        errorTask = 0
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