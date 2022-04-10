package me.aguo.plugin.oldyoungradio.listener

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.IdeFrame
import me.aguo.plugin.oldyoungradio.service.StatusService

class CustomApplicationListener : ApplicationActivationListener {
    private val logger = Logger.getInstance(CustomApplicationListener::class.java)

    override fun applicationActivated(ideFrame: IdeFrame) {
        if (StatusService.instance.statusFuture == null) {
            logger.warn("StatusService -> statusFuture is null.")
            logger.warn("Try to start the statusFuture")
            StatusService.instance.start()
        } else {
            logger.info("StatusService -> statusFuture is running.")
        }
        super.applicationActivated(ideFrame)
    }
}