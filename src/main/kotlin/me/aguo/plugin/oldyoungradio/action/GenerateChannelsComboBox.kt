package me.aguo.plugin.oldyoungradio.action

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import me.aguo.plugin.oldyoungradio.CURRENT_CHANNEL
import me.aguo.plugin.oldyoungradio.Channels
import javax.swing.JComponent

class GenerateChannelsComboBox : ComboBoxAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = CURRENT_CHANNEL.title
        super.update(e)
    }

    override fun createPopupActionGroup(button: JComponent?): DefaultActionGroup {
        val group = DefaultActionGroup()
        val actions = Channels.values().map {
            ChannelAction(it)
        }
        if (ActionManager.getInstance().getActionIdList("CHANNEL").isEmpty()) {
            actions.map {
                ActionManager.getInstance().registerAction(it.channel.name, it)
            }
        }
        group.addAll(actions)
        return group
    }

}
