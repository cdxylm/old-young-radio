@file:Suppress("UnstableApiUsage")

package me.aguo.plugin.oldyoungradio.ui

import com.intellij.openapi.wm.IconLikeCustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.util.concurrency.EdtExecutorService
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.update.Activatable
import com.intellij.util.ui.update.UiNotifyConnector
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.swing.Icon
import javax.swing.JComponent


const val MY_CUSTOM_STATUS_BAR_WIDGET_ID = "Old-Young-Radio"

class MyCustomStatusBarWidget : IconLikeCustomStatusBarWidget, Activatable {

    private var iconIndex = 0
    private val icons: List<Icon> = ContainerUtil.immutableList(
        PluginIcons.mainIconGray,
        PluginIcons.mainIcon,
    )
    private var statusBar: StatusBar? = null
    private var myFuture: ScheduledFuture<*>? = null
    private val myComponent = TextPanel.WithIconAndArrows().apply {
        border = StatusBarWidget.WidgetBorder.ICON
        UiNotifyConnector(this, this@MyCustomStatusBarWidget)
        //TODO: 增加其他功能 鼠标事件监听之类的
    }

    private fun init() {
        iconIndex = 0
        myComponent.toolTipText = null
        myComponent.icon = icons[iconIndex]
    }

    override fun dispose() {
        statusBar = null
    }

    override fun ID(): String = MY_CUSTOM_STATUS_BAR_WIDGET_ID

    override fun install(statusBar: StatusBar) {
        update()
        this.statusBar = statusBar
    }

    override fun getComponent(): JComponent = myComponent

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = null

    override fun showNotify() {
        myFuture = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(
            {
                this.update()
                statusBar?.updateWidget(MY_CUSTOM_STATUS_BAR_WIDGET_ID)
            }, 1, 500, TimeUnit.MILLISECONDS
        )
    }

    override fun hideNotify() {
        if (myFuture != null) {
            myFuture!!.cancel(true)
            myFuture = null
        }
    }

    @Suppress("UnstableApiUsage")
    private fun update() {
        if (!myComponent.isShowing) {
            return
        }
        if (PLAYING_ROOM.room_id == -99) {
            init()
            return
        }
        myComponent.icon = icons[1]
        myComponent.toolTipText = PLAYING_ROOM.title
//        if (iconIndex == icons.size - 1) {
//            iconIndex = 0
//        } else {
//            iconIndex += 1
//        }
    }
}
