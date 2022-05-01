package me.aguo.plugin.oldyoungradio

import com.intellij.ide.util.RunOnceUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.textFieldWithBrowseButton
import com.intellij.ui.layout.panel
import me.aguo.plugin.oldyoungradio.notification.CustomNotifications
import me.aguo.plugin.oldyoungradio.service.PlayerService
import me.aguo.plugin.oldyoungradio.service.RoomsService
import java.awt.event.ItemEvent
import javax.swing.JComponent

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
    private val extraOptions = JBTextField().apply {
        toolTipText = "Some extra options"
    }
    private val formats = arrayOf("flv", "ts", "fmp4")

    //TODO("增加自定义额外选项参数")
    val formatComboBox = ComboBox(formats).apply {
        addItemListener { p0 ->
            extraOptions.text = roomsService.state.settings["${p0.item}Options"]
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
                    .comment("Flv or ts format is recommended. About format and options: <a href='https://plugins.jetbrains.com/plugin/18850-old-young-radio/faq'>FAQ</a>")
                cell {
                    label("options:")
                    extraOptions()
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
        val formatOptions = roomsService.state.settings["${format}Options"].toString()
        return path != pathChooser.text || format != formatComboBox.selectedItem || formatOptions != extraOptions.text
    }

    override fun apply() {
        val old = roomsService.state.settings["vlcDirectory"].toString()
        val new = pathChooser.text
        roomsService.state.settings["vlcDirectory"] = new
        val format = formatComboBox.selectedItem?.toString()
        format?.let {
            roomsService.state.settings["format"] = it
            roomsService.state.settings["${it}Options"] = extraOptions.text.trim()
        }
        if (old != new) {
            logger.warn("VLC directory changed: $old -> $new ")
            CustomNotifications.restartApp()
        }
        PlayerService.instance.initPlayer()
        PlayerService.instance.cancelTimeChangedFuture()
    }

    override fun getDisplayName(): String {
        return "Old Young Radio"
    }


    override fun reset() {
        // setting 打开时会自动执行一次reset 这里涉及的各部件可以不用手动设置初始内容
        roomsService = RoomsService.instance
        pathChooser.text = roomsService.state.settings["vlcDirectory"].toString()
        formatComboBox.selectedItem = roomsService.state.settings["format"].toString()
        extraOptions.text = roomsService.state.settings["${formatComboBox.selectedItem}Options"]
    }

}