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
                options.addAll(it.split(" "))
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
        /*如果不调用，player可能不会进入stopped状态，事件监听器就会重复。
         调用control api ->stop 事件监听器有时会收到两次stopped事件，不知为啥。
         */
        urlIterator = urls.iterator()
        instance.room = room
        val player = instance.getPlayer().mediaPlayer()
        if (player.status().isPlaying) {
            stopVlc()
            Thread.sleep(50)
        }
        player.media().play(urlIterator.next())
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
            此时libvlc实例已经不能进行play、stop之类的操作，所以
            将定时任务取消，同时把readyPlayNext设置为false，以阻止实例进行play的操作，避免ui冻结
            如果用户没有在断网后到程序监测到该问题这段时间里没有主动点击stop按钮，
            再发出一个stop操作,确保该实例在网络恢复后能够收到一个stopped的事件
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