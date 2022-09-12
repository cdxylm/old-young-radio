package me.aguo.plugin.oldyoungradio.service

import com.intellij.openapi.components.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import me.aguo.plugin.oldyoungradio.SELECTED_ROOM
import me.aguo.plugin.oldyoungradio.TOOL_WINDOW_ROOMS
import me.aguo.plugin.oldyoungradio.initSelectedRoom
import me.aguo.plugin.oldyoungradio.model.RoomModel
import java.util.concurrent.CopyOnWriteArrayList

@State(name = "RoomsData", storages = [Storage("roomsData.xml", roamingType = RoamingType.DEFAULT)])
class RoomsService : PersistentStateComponent<RoomsService.State> {
    private val moshi: Moshi = Moshi.Builder().build()
    private val jsonAdapter: JsonAdapter<RoomModel> = moshi.adapter(RoomModel::class.java)

    data class State(
        var roomList: MutableList<String> = CopyOnWriteArrayList(),
        var settings: MutableMap<String, String> = mutableMapOf(
            "vlcDirectory" to "",
            "format" to "",
            "flvOptions" to "",
            "tsOptions" to "",
            "fmp4Options" to "",
        )
    )

    companion object {

        val instance: RoomsService
            get() = ServiceManager.getService(RoomsService::class.java)

    }

    private var state = State()

    fun add(room: RoomModel) {
        with(state.roomList) {
            add(jsonAdapter.toJson(room))
        }
        TOOL_WINDOW_ROOMS.add(room)
    }


    fun remove(room: RoomModel) {
        TOOL_WINDOW_ROOMS.remove(room)
        val tempRoom = RoomModel(room.room_id, room.short_id, room.uid, "", "", 2)
        state.roomList.remove(jsonAdapter.toJson(tempRoom))
        SELECTED_ROOM = initSelectedRoom()
    }

    override fun getState(): State {
        return state
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        state = stateLoadedFromPersistence
    }

}