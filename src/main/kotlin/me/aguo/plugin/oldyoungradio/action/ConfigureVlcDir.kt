package me.aguo.plugin.oldyoungradio.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import me.aguo.plugin.oldyoungradio.RadioSettings

class ConfigureVlcDir : AnAction("Select the Directory", "Configuring vlc directory", AllIcons.General.Settings) {
    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().editConfigurable(e.project!!, RadioSettings())
    }
}