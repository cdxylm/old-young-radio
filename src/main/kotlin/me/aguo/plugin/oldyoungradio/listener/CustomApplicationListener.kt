package me.aguo.plugin.oldyoungradio.listener

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.IdeFrame
import me.aguo.plugin.oldyoungradio.service.StatusService

class CustomApplicationListener : ApplicationActivationListener {
    private val logger = Logger.getInstance(CustomApplicationListener::class.java)

    override fun applicationActivated(ideFrame: IdeFrame) {
        StatusService.instance.apply {
            if (lastRefreshTime == null && statusFuture == null) {
                start()
            } else if (lastRefreshTime != null && System.currentTimeMillis() - lastRefreshTime!! > 2_0000) {
                logger.warn("StatusService seems to have encountered problems.")
                logger.warn("Try to restart the statusFuture.")
                restart()
            } else {
                logger.info("StatusService seems to be work correctly.")
            }
        }
        super.applicationActivated(ideFrame)
    }
}