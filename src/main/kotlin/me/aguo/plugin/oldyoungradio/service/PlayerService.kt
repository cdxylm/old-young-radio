package me.aguo.plugin.oldyoungradio.service

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.EdtExecutorService
import com.sun.jna.NativeLibrary
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import uk.co.caprica.vlcj.binding.LibVlc
import uk.co.caprica.vlcj.binding.RuntimeUtil
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class PlayerService : Disposable {
    private val logger = Logger.getInstance(PlayerService::class.java)

    init {
        Disposer.register(ApplicationManager.getApplication(), this)
        val customVlcDirectory = RoomsService.instance.state.settings["vlcDirectory"].toString()
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), customVlcDirectory)
        val found = NativeDiscovery().discover()
        logger.warn("Vlcj NativeDiscovery:$found")
        try {
            val version = LibVlc.libvlc_get_version()
            logger.info("VLC VERSION:$version")
        } catch (e: Error) {
            CustomNotifications.noVlc()
        }
    }


    private val options = arrayOf("-I dummy", "--no-video")
    private val factory = MediaPlayerFactory(*options)
    private var myPlayer: CallbackMediaPlayerComponent? = CallbackMediaPlayerComponent(
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
        if (myPlayer == null) {
            myPlayer = CallbackMediaPlayerComponent(
                factory,
                null,
                null,
                false,
                null,
            )
        }
        //TODO: 改用事件监听，取代手动获取State
//        myPlayer.mediaPlayer().events().addMediaEventListener()
        stateFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
            {
                currentState = instance.state()!!.intValue()
//                logger.warn(instance.state()!!.name)
            }, 1, 500, TimeUnit.MILLISECONDS
        )
        logger.warn("State future started.")
        return myPlayer as CallbackMediaPlayerComponent
    }

    private fun state(): State? {
        return myPlayer?.mediaPlayer()?.status()?.state()
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
            myPlayer?.mediaPlayer()?.controls()?.stop()
            currentState = instance.state()!!.intValue()
            if (stateFuture != null) {
                logger.warn("State future cancelled.")
                stateFuture!!.cancel(true)
                stateFuture = null
            }
        }
    }

    override fun dispose() {
        stopVlc()
        myPlayer?.mediaPlayer()?.release()
        myPlayer?.release()
        myPlayer = null
    }

}