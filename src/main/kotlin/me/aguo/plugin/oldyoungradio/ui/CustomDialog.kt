package me.aguo.plugin.oldyoungradio.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel


@Suppress("UnstableApiUsage")
class CustomDialog : DialogWrapper(true) {
    data class Model(var inputString: String = "")

    val model = Model()
    private val textArea = JBTextArea(5, 40)

    init {
        init()
        title = "新增房间（多个房间号用逗号隔开）"
    }


    // For compatibility,reducing dependence,kotlin dsl is not used.
    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())
        dialogPanel.preferredSize = Dimension(400, 150)
        dialogPanel.add(textArea, BorderLayout.CENTER)
        return dialogPanel
    }

    override fun doOKAction() {
        super.doOKAction()
        model.inputString = textArea.text
    }


}


