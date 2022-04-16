package me.aguo.plugin.oldyoungradio

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.textFieldWithBrowseButton
import com.intellij.ui.layout.panel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import me.aguo.plugin.oldyoungradio.service.RoomsService
import javax.swing.JComponent

class RadioSettings : Configurable {

    private val logger = Logger.getInstance(RadioSettings::class.java)

    private var roomsService = RoomsService.instance
    val pathChooser = textFieldWithBrowseButton(
        null, null,
        fileChooserDescriptor = FileChooserDescriptor(
            false,
            true,
            false,
            false,
            false,
            false
        ),
    )

    override fun createComponent(): JComponent {
        val panel = panel {
            row("VLC directory:") {
                pathChooser()
                    .comment("Choose the directory that contains the libvlc and libvlccore shared objects (shared objects being e.g. the files whose name ends .so on Linux and .dll on Windows).")

            }
        }
        return panel
    }

    override fun isModified(): Boolean {
        val path = roomsService.state.settings["vlcDirectory"].toString()
        return path != pathChooser.text
    }

    override fun apply() {
        val old = roomsService.state.settings["vlcDirectory"].toString()
        val new = pathChooser.text
        roomsService.state.settings["vlcDirectory"] = new
        logger.warn("VLC directory changed: $old -> $new ")
        CustomNotifications.restartApp()
    }

    override fun getDisplayName(): String {
        return "Old Young Radio"
    }


    override fun reset() {
        roomsService = RoomsService.instance
        pathChooser.text = roomsService.state.settings["vlcDirectory"].toString()
    }

}