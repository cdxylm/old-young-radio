@file:Suppress("UnstableApiUsage")

package me.aguo.plugin.oldyoungradio.ui

import com.intellij.openapi.wm.IconLikeCustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.util.concurrency.EdtExecutorService
import com.intellij.util.ui.update.Activatable
import com.intellij.util.ui.update.UiNotifyConnector
import com.jetbrains.rd.swing.mouseClicked
import me.aguo.plugin.oldyoungradio.PLAYING_ROOM
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.swing.JComponent


const val MY_CUSTOM_STATUS_BAR_WIDGET_ID = "BiliBili-Radio"

class MyCustomStatusBarWidget : IconLikeCustomStatusBarWidget, Activatable {

    private var iconIndex = 0
    private val icons = listOf(
        CatIcons.darkCatIcon_0,
        CatIcons.darkCatIcon_1,
        CatIcons.darkCatIcon_2,
        CatIcons.darkCatIcon_3,
        CatIcons.darkCatIcon_4,
    )
    private var statusBar: StatusBar? = null
    private var myFuture: ScheduledFuture<*>? = null
    private val myComponent = TextPanel.WithIconAndArrows().apply {
        border = StatusBarWidget.WidgetBorder.WIDE
        UiNotifyConnector(this, this@MyCustomStatusBarWidget)
        mouseClicked()
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
            }, 1, 200, TimeUnit.MILLISECONDS
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
        if (myComponent.isShowing) {
            return
        }
        myComponent.icon = icons[iconIndex]
        myComponent.toolTipText = PLAYING_ROOM.title
        if (iconIndex == 4) {
            iconIndex = 0
        } else {
            iconIndex += 1
        }
    }
}
