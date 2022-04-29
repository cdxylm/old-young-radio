package me.aguo.plugin.oldyoungradio.service

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.sun.jna.NativeLibrary
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.listener.CustomMediaPlayerEventAdapter
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import uk.co.caprica.vlcj.binding.LibVlc
import uk.co.caprica.vlcj.binding.RuntimeUtil
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class PlayerService : Disposable {
    private val logger = Logger.getInstance(PlayerService::class.java)

    init {
        Disposer.register(ApplicationManager.getApplication(), this)
        val customVlcDirectory = RoomsService.instance.state.settings["vlcDirectory"].toString()
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), customVlcDirectory)
        try {
            val version = LibVlc.libvlc_get_version()
            logger.info("VLC VERSION:$version")
        } catch (e: Error) {
            CustomNotifications.noVlc()
        }
    }

    private var timeNotChanged = 0
    private var oldTime = 0L
    private val options = arrayOf("-I dummy", "--no-video")
    private val factory = MediaPlayerFactory(*options)
    private var myPlayer: CallbackMediaPlayerComponent? = CallbackMediaPlayerComponent(
        factory,
        null,
        null,
        false,
        null,
    )

    private var timeChangedFuture: ScheduledFuture<*>? = null


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
        return myPlayer as CallbackMediaPlayerComponent
    }


    fun playVlc(urls: List<String>, room: RoomModel) {
        stopVlc()
        timeChangedFuture = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
            { checkTimeChanged() }, 10_000, 500, TimeUnit.MILLISECONDS
        )
        val extraOption: Array<String> = arrayOf()
        when (RoomsService.instance.state.settings["format"].toString()) {
            "flv" -> {}
            "ts" -> {}
            "fmp4" -> {}
        }
        val urlIterator = urls.iterator()
        val player = instance.getPlayer().mediaPlayer()
        player.events()
            .addMediaPlayerEventListener(CustomMediaPlayerEventAdapter(urlIterator, room, timeNotChanged))
        player.media().play(urlIterator.next(), *extraOption)
        player.titles().setTitle(room.room_id)
        PLAYING_ROOM = room
    }

    fun stopVlc() {
        if (myPlayer?.mediaPlayer()?.status()?.isPlaying == true) {
            myPlayer?.mediaPlayer()?.controls()?.stop()
        }
        if (timeChangedFuture != null) {
            timeChangedFuture!!.cancel(true)
            timeChangedFuture = null
        }
    }

    private fun checkTimeChanged() {
        myPlayer?.let {
            val newTime = it.mediaPlayer().status().time()
            if (oldTime == newTime) {
                timeNotChanged += 1
            } else {
                timeNotChanged = 0
            }
            if (timeNotChanged > 60) {
                logger.warn("The newTime hasn't changed for a long time, try to stopVlc.")
                stopVlc()
                timeNotChanged = 0
            }
            oldTime = newTime
        }
    }


    override fun dispose() {
        stopVlc()
        myPlayer?.mediaPlayer()?.release()
        myPlayer?.release()
        myPlayer = null
    }

}