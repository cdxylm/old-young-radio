package me.aguo.plugin.oldyoungradio

import com.intellij.ide.util.RunOnceUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.textFieldWithBrowseButton
import com.intellij.ui.layout.panel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import me.aguo.plugin.oldyoungradio.service.RoomsService
import java.awt.event.ItemEvent
import javax.swing.JComponent
import javax.swing.JSpinner

class RadioSettings : Configurable {

    private val logger = Logger.getInstance(RadioSettings::class.java)
    private var roomsService = RoomsService.instance


    init {
        RunOnceUtil.runOnceForApp("SetFormatToTs") {
            roomsService.state.settings["format"] = "flv"
        }
    }

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
    val pcr = JSpinner().apply {
        value = 0
        toolTipText = "PTS Delay(ms)"
    }
    private val formats = arrayOf("flv", "ts", "fmp4")

    //TODO("增加自定义额外选项参数")
    val formatComboBox = ComboBox(formats).apply {
        selectedItem = "flv"
        addItemListener { p0 ->
            pcr.isVisible = p0?.item == "TODO"
        }
    }

    val recentlyMrls = JBTextArea().apply {
        text = CURRENT_STREAM_URLS.joinToString("\n\n")
        rows = CURRENT_STREAM_URLS.size
        lineWrap = true
        wrapStyleWord = true
    }

    val showMrls = JBCheckBox().apply {
        text = "Show"
        isSelected = false
        addItemListener {
            recentlyMrls.isVisible = it.stateChange == ItemEvent.SELECTED
        }
    }

    override fun createComponent(): JComponent {
        val panel = panel {
            row("VLC directory:") {
                pathChooser()
                    .comment("Choose the directory that contains the libvlc and libvlccore shared objects (shared objects being e.g. the files whose name ends .so on Linux and .dll on Windows).")

            }
            row("Stream format:") {
                formatComboBox().focused()
                    .comment("Flv or ts format is recommended. It often takes a few seconds to open a stream in ts format. Some MRLs are not playable in vlc.")
                cell(isFullWidth = false) {
                    pcr().visible(false)
                }
            }
            row("Recent MRLs:") {
                showMrls()
            }
            row {
                recentlyMrls().visible(false)
            }
        }
        return panel
    }

    override fun isModified(): Boolean {
        val path = roomsService.state.settings["vlcDirectory"].toString()
        val format = roomsService.state.settings["format"].toString()
        return path != pathChooser.text || format != formatComboBox.selectedItem
    }

    override fun apply() {
        val old = roomsService.state.settings["vlcDirectory"].toString()
        val new = pathChooser.text
        roomsService.state.settings["vlcDirectory"] = new
        roomsService.state.settings["format"] = formatComboBox.selectedItem as String
        if (old != new) {
            logger.warn("VLC directory changed: $old -> $new ")
            CustomNotifications.restartApp()
        }
    }

    override fun getDisplayName(): String {
        return "Old Young Radio"
    }


    override fun reset() {
        roomsService = RoomsService.instance
        pathChooser.text = roomsService.state.settings["vlcDirectory"].toString()
        formatComboBox.selectedItem = roomsService.state.settings["format"].toString()
    }

}