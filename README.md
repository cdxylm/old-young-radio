# old-young-radio

![Build](https://github.com/cdxylm/old-young-radio/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/18850)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/18850)

## Template ToDo list

- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Verify the [pluginGroup](/gradle.properties), [plugin ID](/src/main/resources/META-INF/plugin.xml)
  and [sources package](/src/main/kotlin).
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate)
  for the first time.
- [ ] Set the Plugin ID in the above README badges.
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified
  about releases containing new features and fixes.

## 功能

- [x] 存储房间相关信息
- [x] 刷新、增加、删除房间信息
- [x] 显示房间信息
- [x] 播放、停止
- [x] 开播提醒

为了方便控制使用了VLCJ包，它会自动发现本地的VLC。
<!-- Plugin description -->
English:

This plugin is about [bilibili live](https://live.bilibili.com).

You can:

- Subscribe bilibili live room.
- Get real-time room status.
- Play the audio of the live stream in the background.

**Play function requires [VLC](https://www.videolan.org/vlc/) player to be installed.**

中文：

因为有听直播唱见的习惯，但是又不想在浏览器中打开，以前是用脚本获取直播流调用Pot Player播放。

后来觉得画面也不需要，于是直接做一个插件到IDE中去，后台播放音频。

**播放功能需要安装 [VLC](https://www.videolan.org/vlc/) 播放器**
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "
  old-young-radio"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/cdxylm/old-young-radio/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
