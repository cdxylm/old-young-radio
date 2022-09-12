package me.aguo.plugin.oldyoungradio.listener

import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.getHost
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import me.aguo.plugin.oldyoungradio.service.PlayerService
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter

class CustomMediaPlayerEventAdapter :
    MediaPlayerEventAdapter() {
    override fun playing(mediaPlayer: MediaPlayer?) {
        PLAYING_ROOM = PlayerService.instance.room
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        val iterator = PlayerService.instance.urlIterator
        if (iterator.hasNext()) {
            mediaPlayer?.submit {
                val currentUrl = getHost(iterator.next())
                PlayerService.instance.tailOptions.replaceAll {
                    if (it.startsWith(":http-host")) ":http-host='https://${currentUrl[1]}'" else it
                }
                mediaPlayer.media().play(currentUrl[0], *PlayerService.instance.tailOptions.toTypedArray())
                PlayerService.instance.timeNotChanged = 0
                PLAYING_ROOM = PlayerService.instance.room
            }
        } else {
            CustomNotifications.noUrl("为您尝试了该格式下所有链接，仍未成功")
            PLAYING_ROOM = RoomModel(-99, -99, -99)
            PlayerService.instance.cancelTimeChangedFuture()
            if (!PlayerService.instance.readyPlayNext) {
                PlayerService.instance.readyPlayNext = true
                CustomNotifications.playerReady()
            }
        }
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        PLAYING_ROOM = RoomModel(-99, -99, -99)
        PlayerService.instance.stopping = false
        PlayerService.instance.cancelTimeChangedFuture()
        if (!PlayerService.instance.readyPlayNext) {
            PlayerService.instance.readyPlayNext = true
            CustomNotifications.playerReady()
        }
    }

    override fun finished(mediaPlayer: MediaPlayer?) {
        PLAYING_ROOM = RoomModel(-99, -99, -99)
        PlayerService.instance.cancelTimeChangedFuture()
    }

    override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
        PlayerService.instance.newTime = newTime
    }
}