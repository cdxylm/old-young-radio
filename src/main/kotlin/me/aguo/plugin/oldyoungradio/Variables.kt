package me.aguo.plugin.oldyoungradio

import com.intellij.ui.CollectionListModel
import me.aguo.plugin.oldyoungradio.model.RoomModel


val TOOL_WINDOW_ROOMS = CollectionListModel<RoomModel>()
var SELECTED_ROOM = RoomModel(-99, -99, -99, "", "", 2)
var PLAYING_ROOM = RoomModel(-99, -99, -99, "", "", 2)
var CURRENT_STREAM_URLS = listOf<String>()



