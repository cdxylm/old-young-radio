package me.aguo.plugin.oldyoungradio.service

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.EdtExecutorService
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.model.RoomModel
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class PlayerService : Disposable {

    init {
        Disposer.register(ApplicationManager.getApplication(), this)
    }

    private val logger = Logger.getInstance(PlayerService::class.java)

    private val options = arrayOf("-I dummy", "--no-video")
    private val factory = MediaPlayerFactory(*options)
    private val myPlayer = CallbackMediaPlayerComponent(
        factory,
        null,
        null,
        false,
        null,
    )
    private var currentState: Int by Delegates.vetoable(
        0
    ) { _, oldValue, newValue ->
        if (newValue !in listOf(1, 3)) {
            PLAYING_ROOM = RoomModel(-99, -99, -99)
        }
        newValue != oldValue
    }

    private var stateFuture: ScheduledFuture<*>? = null


    companion object {
        val instance by lazy {
            PlayerService()
        }
    }

    private fun getPlayer(): CallbackMediaPlayerComponent {
        stateFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
            {
                currentState = instance.state()!!.intValue()
//                logger.warn(instance.state()!!.name)
            }, 1, 500, TimeUnit.MILLISECONDS
        )
        logger.warn("State future started.")
        return myPlayer
    }

    private fun state(): State? {
        return myPlayer.mediaPlayer().status().state()
    }

    fun playVlc(urls: List<String>, room: RoomModel) {
        val suitableUrl = urls.filter {
            it.indexOf("gotcha03") != -1
        }
        if (suitableUrl.isEmpty()) {
            @Suppress("DialogTitleCapitalization")
            val notification = Notification(
                "Old Young Radio",
                "播放错误",
                "没有找到合适的播放流",
                NotificationType.WARNING
            )
            Notifications.Bus.notify(notification)
            return
        }
        for (i in suitableUrl) {
            if (i.indexOf("gotcha03") != -1) {
                instance.getPlayer().mediaPlayer().media().play(i)
                PLAYING_ROOM = room
            }
        }
    }

    fun stopVlc() {
        if (instance.currentState == 3) {
            myPlayer.mediaPlayer().controls().stop()
            currentState = instance.state()!!.intValue()
            if (stateFuture != null) {
                logger.warn("State future cancelled.")
                stateFuture!!.cancel(true)
                stateFuture = null
            }
        }
    }

    override fun dispose() {
        myPlayer.mediaPlayer().release()
    }

}