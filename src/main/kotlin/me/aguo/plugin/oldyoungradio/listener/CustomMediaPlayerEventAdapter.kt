package me.aguo.plugin.oldyoungradio.listener

import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter

class CustomMediaPlayerEventAdapter(
    private val urlIterator: Iterator<String>,
    private val room: RoomModel,
    private var timeNotChanged: Int,
    private var oldTime: Long
) :
    MediaPlayerEventAdapter() {
    override fun playing(mediaPlayer: MediaPlayer?) {
//        println("playing")
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        if (urlIterator.hasNext()) {
            mediaPlayer?.submit {
                mediaPlayer.media().play(urlIterator.next())
                PLAYING_ROOM = room
            }
        } else {
            CustomNotifications.noUrl("为您尝试了该格式下所有链接，仍未成功")
            mediaPlayer?.events()?.removeMediaPlayerEventListener(this)
        }
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.events()?.removeMediaPlayerEventListener(this)
        PLAYING_ROOM = RoomModel(-99, -99, -99)
    }

    override fun finished(mediaPlayer: MediaPlayer?) {
//        println("finished")
    }

    override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
        if (oldTime == newTime) {
            timeNotChanged += 1
        } else {
            timeNotChanged = 0
        }
        if (timeNotChanged > 10) {
            mediaPlayer?.submit {
                mediaPlayer.controls().stop()
            }
        }
        oldTime = newTime
    }

}