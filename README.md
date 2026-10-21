# 取件助手

聚合菜鸟、淘宝、拼多多、京东、小红书、美团、抖音的取件入口，点击后优先通过 Deep Link 直达官方 App 的取件码或待取列表页面。

## 功能

- 主界面一屏聚合 7 个平台入口，按「打开身份码」和「查看待取」分组
- 打开策略逐级降级：App Deep Link → 官方 App 首页 → H5 页面
- 支持深色模式、固定桌面快捷方式（Android 8.0+）、桌面小组件
- `web/` 提供同功能的 PWA 备用版本

## 目录结构

```
app/src/main/java/com/pickup/tool/   Android 源码
  Destination.java                   平台清单、展示文案与深链/H5 地址（唯一数据源）
  DeepLinkLauncher.java              打开策略与降级逻辑
  MainActivity.java                  入口解析与深色偏好
  HomeView.java                      首页视图构建
  Theme.java                         深浅色调色板
  Dates.java                         日期展示纯函数
  PickupWidgetProvider.java          桌面小组件
  ShortcutPinning.java               固定快捷方式
  M3.java / SurfaceDrawable.java     Material 3 配色与卡片背景
app/src/main/res/                    资源（图标、快捷方式、小组件布局）
app/src/test/java/                   单元测试
web/                                 静态 PWA
build_scripts/build_manual.py        不依赖 Gradle 的手工打包脚本
build_scripts/sync_web.py            从 Destination 生成 Web 入口与 PWA 快捷方式
docs/screenshots/                    历史版本截图
dist/                                发布 APK（不入库）
```

## 构建

环境要求：JDK 17+、Android SDK Platform 35、Build-Tools 36.0.0。

Gradle 构建（推荐）：

```powershell
.\gradlew.bat assembleDebug
```

Release 签名：将 `keystore.properties.example` 复制为 `keystore.properties` 并填写真实信息；该文件已被 `.gitignore` 排除，不会入库。

手工构建（无 Gradle）：

```powershell
$env:PICKTOOL_JDK = "C:\path\to\jdk-17"
$env:ANDROID_HOME = "C:\path\to\android-sdk"
python build_scripts/build_manual.py
```

手工脚本的 `versionCode`、`versionName` 和 Java 编译级别均从 `app/build.gradle.kts` 读取，不单独维护。

单元测试：

```powershell
.\gradlew.bat testDebugUnitTest
```

## 维护入口

- 新增或调整平台：修改 `Destination.java`；首页入口顺序在 `HomeView.java`，Web 与 PWA 快捷方式运行 `py build_scripts/sync_web.py` 生成
- 调整桌面快捷方式：`ShortcutPinning.java` 与 `res/xml/shortcuts.xml`
- 调整桌面小组件：`PickupWidgetProvider.java` 与 `res/layout/pickup_widget.xml`
- 调整首页样式：`HomeView.java` 与 `Theme.java`
- 调整 PWA 静态样式：`web/index.html`（入口区由脚本生成，不要手改标记之间内容）

## 版本与发布

- 版本号统一在 `app/build.gradle.kts` 中维护（当前 `2.1.0` / `210`）
- `dist/` 中的 APK 通过 GitHub Release 分发，不进入版本库
- `docs/screenshots/` 为历史界面截图，可能落后于当前实现

## 免责声明

第三方应用更新后，其内部页面地址可能发生变化，导致深链失效。本项目与各平台官方无关联。
