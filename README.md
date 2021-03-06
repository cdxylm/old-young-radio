# old-young-radio

![Build](https://github.com/cdxylm/old-young-radio/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18850.svg)](https://plugins.jetbrains.com/plugin/18850)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18850.svg)](https://plugins.jetbrains.com/plugin/18850)


## 功能

- [x] 存储房间相关信息
- [x] 刷新、增加、删除房间信息
- [x] 显示房间信息
- [x] 播放、停止
- [x] 开播提醒

为了方便控制使用了VLCJ包，它会自动发现本地的VLC。
<!-- Plugin description -->
Listen to `Bilibili Live` in IDE.

在IDE中听 `B站` 直播。
### English:

This plugin is about [bilibili live](https://live.bilibili.com).

You can:

- Subscribe bilibili live room.
- Get real-time room status.
- Play the audio of the live stream in the background.
**Play function requires [VLC](https://www.videolan.org/vlc/) player to be installed.**
**If you get an error about Libvlc when you double-click on the room(play action), you may need to configure the vlc directory manually**
<kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>Old Young Radio</kbd> > <kbd>VLC directory</kbd>
#### Usage:

##### Subscribe:

Open [bilibili-live](https://live.bilibili.com/) in browser.

Choose the room what you want to subscribe. eg: [央视新闻](https://live.bilibili.com/21686237)

Copy the room id in URL bar.The number after "https://live.bilibili.com/" （21686237） is the room id.

Click "Add room" button <img src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/16/000000/external-plus-user-interface-tanah-basah-glyph-tanah-basah-2.png">
in the Radio Tool Window and paste the room id.

##### Remove room:
Select the room item in Radio Tool Window.

Then the "Remove room" button <img src="https://img.icons8.com/material-rounded/16/000000/minus.png"> is available.
Click it.

##### Refresh rooms' status manually:
Click the "Refresh" button <img src="https://img.icons8.com/ios-glyphs/16/000000/refresh--v1.png">

##### Play:
Double-click the room item which it's online(the item icon is green.)

##### Stop:
When the plugin is playing some streaming media,the stop button <img src="https://img.icons8.com/office/16/000000/stop.png"> is available.
Click the button.


### 中文：

因为有听直播唱见的习惯，但是又不想在浏览器中打开，以前是用脚本获取直播流调用Pot Player播放。

后来觉得画面也不需要，于是直接做一个插件到IDE中去，后台播放音频。

**播放功能需要安装 [VLC](https://www.videolan.org/vlc/) 播放器**
**如果双击播放的时候提示Libvlc相关的错误，请手动配置vlc目录，重启应用再试。**
<kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>Old Young Radio</kbd> > <kbd>VLC directory</kbd>
#### 使用：

点击AddRoom <img src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/16/000000/external-plus-user-interface-tanah-basah-glyph-tanah-basah-2.png"> 输入房间号（支持短号，如英雄联盟赛事房间号7734200，短号为6）订阅房间。房间号可批量输入，用中英文逗号隔开即可。
也支持复制用户主页链接，从关注列表中导入（需要公开关注列表，默认是公开的；只能获取前5页的信息。）<br>
也可以从热门面板右键订阅。

点击RemoveRoom <img src="https://img.icons8.com/material-rounded/16/000000/minus.png"> 删除房间。<br>

点击 Refresh <img src="https://img.icons8.com/ios-glyphs/16/000000/refresh--v1.png"> 手动刷新房间状态。<br>

双击在线（房间前面的图标为绿色）的房间进行播放。

点击 Stop <img src="https://img.icons8.com/office/16/000000/stop.png"> 停止播放。


默认每15秒刷新一次已订阅的房间状态，目前无法自定义间隔。

<!-- Plugin description end -->
### 常见问题：
- 后台刷新状态的Api每隔一段时间(几个小时)可能会收到如下错误，不影响正常使用。
v0.0.3中已经解决
  ```log
  java.io.IOException: /192.168.x.x:port: GOAWAY received
  ```

- 通知 > 播放出错：尝试了所有链接

  因为部分地址在vlc中不能播放，插件尝试了所有API返回的地址后仍收到错误时将会弹出该通知。


- 通知 > 播放出错：没有合适的链接

  没有获取到地址时会弹出该通知。已知每次请求API得到的直播流地址（包括数量）都可能不同，更常见的是部分直播间返回的地址中不包含fmp4格式地址。


- 没有任何提醒播放失败（终止）

  插件不能捕获到vlc播放中发出的错误或警告，不同操作系统、vlc版本甚至是网络环境，在播放同一地址的表现都可能不同。

  个人在Ubuntu上播放fmp4格式下的地址时会遇到如下错误（不同的流延迟也不尽相同）：
  ```log
  ES_OUT_SET_(GROUP_)PCR  is called too late (pts_delay increased to 1000 ms)
  ```
  因为时钟不同步，这会导致几秒钟后播放停止，实际使用vlc的时候，我可以通过添加 ```--sout-ts-pcr 1000 ```参数来解决这个问题，而在我的Windows系统上，根本就不会出现这个问题。

  就像上面所说的，插件不能捕获到这类非打开流时vlc抛出的异常，因此很难在插件内部给出解决方案。我能想到的一个办法是给出一个接口，让插件用户可以自定义一些额外的播放参数，来解决用户遇到的问题，这可能会在未来的版本实现。
  这也是为什么在以前的版本中插件会过滤掉一些直播流地址。

  如果你遇到了奇怪的错误，我的建议是，在设置里找到最近的直播流地址，手动在vlc中尝试播放，同时观察是否有任何异常。
  
  显示最近尝试播放的直播间的直播流地址:
  <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>Old Young Radio</kbd> > <kbd>Recent MRLs</kbd> >
  <kbd>Check "Show"</kbd>

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
