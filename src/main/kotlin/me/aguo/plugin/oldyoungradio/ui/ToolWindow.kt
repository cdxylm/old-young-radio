package me.aguo.plugin.oldyoungradio.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.panel
import me.aguo.plugin.oldyoungradio.TOOL_WINDOW_ROOMS
import me.aguo.plugin.oldyoungradio.action.AddRoom
import me.aguo.plugin.oldyoungradio.action.RefreshAllRoom
import me.aguo.plugin.oldyoungradio.action.RemoveRoom
import me.aguo.plugin.oldyoungradio.action.StopRadio
import me.aguo.plugin.oldyoungradio.getAllIds
import me.aguo.plugin.oldyoungradio.model.RoomModel
import me.aguo.plugin.oldyoungradio.network.BiliBiliApi
import me.aguo.plugin.oldyoungradio.service.StatusService


@Suppress("UnstableApiUsage")
class ToolWindow : ToolWindowFactory {
    @Suppress("UNCHECKED_CAST")
    private val rooms = BiliBiliApi.getStatusInfoByUids(getAllIds("uid") as List<Int>)

    init {
        rooms.sortedBy { it?.room_id }.map { TOOL_WINDOW_ROOMS.add(it) }
        StatusService.instance.start()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager

        val simplePanel = SimpleToolWindowPanel(true, true)
        simplePanel.toolbar = createActionToolbar(simplePanel).component
        simplePanel.setContent(radios(TOOL_WINDOW_ROOMS))

        //TODO: toolbar https://plugins.jetbrains.com/docs/intellij/lists-and-trees.html#toolbardecorator
        val toolbar = ToolbarDecorator.createDecorator(JBList(rooms))
        toolbar.setAddAction(AddRoom())
        // TODO

        val content = contentManager.factory.createContent(simplePanel, null, false)
        contentManager.addContent(content)
    }

}

@Suppress("UnstableApiUsage")
fun radios(rooms: CollectionListModel<RoomModel>): DialogPanel {
    val jbList = JBList(rooms)
    jbList.addMouseListener(CustomMouseAdapter())
    jbList.cellRenderer = CustomRenderer()
    val roomsComponent = JBScrollPane(jbList)

    return panel {
        titledRow("已订阅") {
            row {
                roomsComponent()
            }
        }
    }
}


fun createActionToolbar(panel: SimpleToolWindowPanel): ActionToolbar {
    val actionGroup = DefaultActionGroup()
    actionGroup.add(RefreshAllRoom())
    actionGroup.add(AddRoom())
    actionGroup.add(RemoveRoom())
    actionGroup.addSeparator()
    actionGroup.add(StopRadio())
    val actionBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
    actionBar.setTargetComponent(panel)
    return actionBar
}