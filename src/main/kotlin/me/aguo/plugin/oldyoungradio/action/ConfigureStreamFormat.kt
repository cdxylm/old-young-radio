package me.aguo.plugin.oldyoungradio.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import me.aguo.plugin.oldyoungradio.RadioSettings

class ConfigureStreamFormat : AnAction("Change the Format", "Configuring stream format", AllIcons.General.Settings) {
    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().editConfigurable(e.project!!, RadioSettings())
    }
}