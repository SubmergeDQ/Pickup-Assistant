# 取件助手

安卓端取件入口聚合工具：一屏打开菜鸟、淘宝、拼多多、京东、小红书、美团、抖音的取件码或待取列表。

## 功能

- 平台 Deep Link 直达，失败时依次降级到官方 App 和网页
- 深色模式、固定桌面快捷方式、桌面小组件
- 桌面快捷方式与小组件固定菜鸟、淘宝、拼多多、京东、小红书五个高频入口（系统快捷方式上限为 5 个），其余平台可在首页手动固定
- `web/` 下提供 PWA 备用版本，仅保留菜鸟、淘宝、拼多多三个最高频入口

## 构建

环境要求：JDK 17+、Android SDK Platform 35、Build-Tools 36.0.0。

```powershell
.\gradlew.bat assembleDebug
```

单元测试：

```powershell
.\gradlew.bat testDebugUnitTest
```

Release 签名：复制 `keystore.properties.example` 为 `keystore.properties` 并填写。

无 Gradle 环境可用 `py build_scripts/build_manual.py` 打包，版本号从 Gradle 配置读取。

## 维护

- 平台清单与深链：`Destination.java`
- 首页与主题：`HomeView.java`、`Theme.java`
- 修改 `Destination.java` 后运行 `py build_scripts/sync_web.py`，同步 Web 入口和 PWA 快捷方式

## 说明

第三方应用更新后其内部页面地址可能变化，导致深链失效。本项目与各平台官方无关联。
