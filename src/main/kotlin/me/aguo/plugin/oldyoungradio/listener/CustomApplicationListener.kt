package me.aguo.plugin.oldyoungradio.listener

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.IdeFrame
import me.aguo.plugin.oldyoungradio.service.StatusService

class CustomApplicationListener : ApplicationActivationListener {
    private val logger = Logger.getInstance(CustomApplicationListener::class.java)

    override fun applicationActivated(ideFrame: IdeFrame) {
        StatusService.instance.apply {
            if (this.lastRefreshTime == null) {
                this.start()
            } else if (System.currentTimeMillis() - StatusService.instance.lastRefreshTime!! > 2_0000) {
                logger.warn("StatusService seems to have encountered problems.")
                logger.warn("Try to restart the statusFuture.")
                StatusService.instance.restart()
            } else {
                logger.info("StatusService seems to be work correctly.")
            }
        }
        super.applicationActivated(ideFrame)
    }
}