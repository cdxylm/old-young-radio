package me.aguo.plugin.oldyoungradio.ui

import com.intellij.ide.lightEdit.LightEditCompatible
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

@Suppress("UnstableApiUsage")
class MyCustomStatusBarWidgetFactory : StatusBarWidgetFactory, LightEditCompatible {
    override fun getId(): String {
        return MY_CUSTOM_STATUS_BAR_WIDGET_ID
    }

    override fun getDisplayName(): String {
        return "SheepRadio"
    }

    override fun isAvailable(project: Project): Boolean {
        return true
    }

    override fun isEnabledByDefault(): Boolean {
        return false
    }

    override fun createWidget(project: Project): StatusBarWidget {
        return MyCustomStatusBarWidget()
    }

    override fun disposeWidget(widget: StatusBarWidget) {
        if (widget.ID() == MY_CUSTOM_STATUS_BAR_WIDGET_ID) Disposer.dispose(widget)
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        return true
    }
}