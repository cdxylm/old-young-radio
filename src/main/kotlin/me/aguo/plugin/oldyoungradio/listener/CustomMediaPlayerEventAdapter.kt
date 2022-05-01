package me.aguo.plugin.oldyoungradio.listener

import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import me.aguo.plugin.oldyoungradio.service.PlayerService
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter

class CustomMediaPlayerEventAdapter(
    private val urlIterator: Iterator<String>,
    private val room: RoomModel,
    private var timeNotChanged: Int,
) :
    MediaPlayerEventAdapter() {
    override fun playing(mediaPlayer: MediaPlayer?) {
//        println("playing")
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        if (urlIterator.hasNext()) {
            mediaPlayer?.submit {
                mediaPlayer.media().play(urlIterator.next())
                timeNotChanged = 0
                PLAYING_ROOM = room
            }
        } else {
            CustomNotifications.noUrl("为您尝试了该格式下所有链接，仍未成功")
            PLAYING_ROOM = RoomModel(-99, -99, -99)
            mediaPlayer?.events()?.removeMediaPlayerEventListener(this)
        }
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.events()?.removeMediaPlayerEventListener(this)
        PLAYING_ROOM = RoomModel(-99, -99, -99)
        if (!PlayerService.instance.readyPlayNext) {
            PlayerService.instance.readyPlayNext = true
        }
    }

    override fun finished(mediaPlayer: MediaPlayer?) {
        PLAYING_ROOM = RoomModel(-99, -99, -99)
    }
}