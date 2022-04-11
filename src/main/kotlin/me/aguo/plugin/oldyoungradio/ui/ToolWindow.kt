package me.aguo.plugin.oldyoungradio.ui

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import com.intellij.ui.layout.panel
import me.aguo.plugin.oldyoungradio.*
import me.aguo.plugin.oldyoungradio.action.*
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import javax.swing.JPanel


@Suppress("UnstableApiUsage")
class ToolWindow : ToolWindowFactory, DumbAware {
    companion object {
        private const val TAB_SUBSCRIBED_ID = 1
        private const val TAB_CHANNELS_ID = 2
        private const val TAB_SUBSCRIBED_NAME = "已订阅"
        private const val TAB_CHANNELS_NAME = "热门"
    }

    @Suppress("UNCHECKED_CAST")
    private val rooms = BiliBiliApi.getStatusInfoByUids(getAllIds("uid") as List<Int>)

    init {
        rooms.sortedBy { it?.room_id }.map { TOOL_WINDOW_ROOMS.add(it) }
    }

    class ChannelTabSelectedListener : ContentManagerListener {
        override fun selectionChanged(event: ContentManagerEvent) {
            if (CURRENT_CHANNEL != Channels.CHANNEL_ALL && event.content.isSelected && event.content.tabName == TAB_CHANNELS_NAME) {
                val actionManager = ActionManager.getInstance()
                val action = actionManager.getAction(CURRENT_CHANNEL.name)
                action.actionPerformed(
                    AnActionEvent(
                        null,
                        DataManager.getInstance().getDataContext(event.content.component),
                        ActionPlaces.TOOLWINDOW_POPUP,
                        Presentation(), actionManager, 0
                    )
                )
            }
            super.selectionChanged(event)
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager

        val subscribePanel = SimpleToolWindowPanel(true, true).apply {
            toolbar = createRoomActionToolbar(this).component
            setContent(radios(TOOL_WINDOW_ROOMS, TAB_SUBSCRIBED_ID))
        }

        val channelsPanel = SimpleToolWindowPanel(true, true).apply {
            toolbar = createHotChannelsToolbar(this).component
            setContent(radios(CHANNEL_TAB_ROOMS, TAB_CHANNELS_ID))
        }

//        val label = JLabel("Loading...", AnimatedIcon.Default(), SwingConstants.LEFT)
//        val panel = panel {
//        }
//        panel.add(label)
//        simplePanel2.setContent(panel)


        val content = contentManager.factory.createContent(subscribePanel, TAB_SUBSCRIBED_NAME, false)
        val content2 = contentManager.factory.createContent(channelsPanel, TAB_CHANNELS_NAME, false)
        contentManager.addContent(content)
        contentManager.addContent(content2)
        contentManager.addContentManagerListener(ChannelTabSelectedListener())
    }
}

fun radios(rooms: CollectionListModel<RoomModel>, tabId: Int): JPanel {
    val jbList = JBList(rooms)
    jbList.addMouseListener(CustomMouseAdapter(tabId))
    jbList.cellRenderer = CustomRenderer(tabId)
    val roomsComponent = JBScrollPane(jbList)
    return panel {
        row {
            roomsComponent()
        }
    }
}


fun createRoomActionToolbar(panel: SimpleToolWindowPanel): ActionToolbar {
    val actionGroup = DefaultActionGroup()
    actionGroup.apply {
        add(RefreshAllRoom())
        add(AddRoom())
        add(RemoveRoom())
        addSeparator()
        add(StopRadio())
    }

    val actionBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
    actionBar.setTargetComponent(panel)
    return actionBar
}

fun createHotChannelsToolbar(panel: SimpleToolWindowPanel): ActionToolbar {
    val actionGroup = DefaultActionGroup()
    actionGroup.apply {
        add(GenerateChannelsComboBox())
        addSeparator()
        add(StopRadio())
    }
    val actionBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
    actionBar.setTargetComponent(panel)
    return actionBar
}