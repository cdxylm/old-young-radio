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

    var timeNotChanged = 0
    private var oldTime = 0L
    var newTime = 0L
    var myPlayer: CallbackMediaPlayerComponent? = null
    private var factory: MediaPlayerFactory? = null

    private var timeChangedFuture: ScheduledFuture<*>? = null
    var readyPlayNext = true
    var urlIterator: Iterator<String> = listOf("").iterator()
    var room: RoomModel = RoomModel(-99, -99, -99)
    var stopping = false
    var tailOptions = arrayOfNulls<String>(10)

    companion object {
        val instance by lazy {
            PlayerService()
        }
    }

    private fun getPlayer(): CallbackMediaPlayerComponent {
        if (myPlayer == null) {
            val options = mutableListOf("-I dummy", "--no-video")
            val format = RoomsService.instance.state.settings["format"].toString()
            RoomsService.instance.state.settings["${format}Options"]?.let {
                options.addAll(it.split(" ").filter { option -> option.startsWith("--") })
            }
            RoomsService.instance.state.settings["${format}Options"]?.let {
                it.split(" ").filter { option -> !option.startsWith("--") }.forEachIndexed { i, v ->
                    tailOptions[i] = v
                }
            }
            factory = MediaPlayerFactory(options)
            myPlayer = CallbackMediaPlayerComponent(
                factory,
                null,
                null,
                false,
                null,
            )
            myPlayer?.mediaPlayer()?.events()?.addMediaPlayerEventListener(CustomMediaPlayerEventAdapter())
        }
        return myPlayer as CallbackMediaPlayerComponent
    }


    fun playVlc(urls: List<String>, room: RoomModel) {
        /*??????????????????player??????????????????stopped???????????????????????????????????????
         ??????control api ->stop ????????????????????????????????????stopped????????????????????????
         */
        urlIterator = urls.iterator()
        instance.room = room
        val player = instance.getPlayer().mediaPlayer()
        if (player.status().isPlaying) {
            stopVlc()
            Thread.sleep(50)
        }
        player.media().play(urlIterator.next(), *tailOptions)
        timeChangedFuture = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
            { checkTimeChanged() }, 1_0000, 500, TimeUnit.MILLISECONDS
        )
        logger.warn("timeChangedFuture started")
    }

    fun stopVlc() {
        PLAYING_ROOM = RoomModel(-99, -99, -99)
        stopping = true
        if (myPlayer?.mediaPlayer()?.status()?.isPlaying == true) {
            myPlayer?.mediaPlayer()?.let {
                it.submit { it.controls().stop() }
            }
        }
    }

    private fun checkTimeChanged() {
        if (oldTime == newTime) {
            timeNotChanged += 1
        } else {
            timeNotChanged = 0
        }
        if (timeNotChanged > 30) {
            logger.warn("The newTime hasn't changed for a long time, try to stopVlc.")
            /*
            ??????libvlc????????????????????????play???stop????????????????????????
            ?????????????????????????????????readyPlayNext?????????false????????????????????????play??????????????????ui??????
            ??????????????????????????????????????????????????????????????????????????????????????????stop?????????
            ???????????????stop??????,???????????????????????????????????????????????????stopped?????????
            */
            cancelTimeChangedFuture(true)
            if (!stopping) {
                stopVlc()
            }
            if (myPlayer?.mediaPlayer()?.status()?.state()?.intValue() != 3) {
                readyPlayNext = true
            }

        }
        oldTime = newTime

    }

    fun cancelTimeChangedFuture(error: Boolean = false) {
        if (error) {
            readyPlayNext = false
        }
        if (timeChangedFuture != null) {
            timeChangedFuture!!.cancel(true)
            timeChangedFuture = null
            timeNotChanged = 0
            logger.warn("timeChangedFuture cancelled")
        }
    }

    fun initPlayer() {
        stopVlc()
        myPlayer?.release()
        myPlayer = null
        factory?.release()
        factory = null
    }

    override fun dispose() {
        stopVlc()
        myPlayer?.release()
        myPlayer = null
    }

}