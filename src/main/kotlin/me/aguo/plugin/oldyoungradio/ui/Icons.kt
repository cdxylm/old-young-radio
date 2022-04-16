package me.aguo.plugin.oldyoungradio.ui

import com.intellij.openapi.util.IconLoader

object PluginIcons {
    @JvmField
    val mainIcon = IconLoader.getIcon("/icons/bili.svg", javaClass)
    val mainIconGray = IconLoader.getIcon("/icons/bili_gray.svg", javaClass)
    val stopIcon = IconLoader.getIcon("/icons/player_stop.svg", javaClass)
    val onlineIcon = IconLoader.getIcon("/icons/online.svg", javaClass)
    val offlineIcon = IconLoader.getIcon("/icons/offline.svg", javaClass)
}