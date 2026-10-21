package com.pickup.tool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

/**
 * 主界面入口：解析深链跳转请求，维护深浅色偏好，界面构建交给 {@link HomeView}。
 */
public final class MainActivity extends Activity {
    static final String ACTION_OPEN = "com.pickup.tool.OPEN";
    /** 静态快捷方式使用的 action 前缀，后缀为 {@link Destination#key} 的大写形式。 */
    static final String ACTION_OPEN_PREFIX = "com.pickup.tool.OPEN_";
    static final String EXTRA_DESTINATION = "destination";

    /** 深浅色偏好存储。 */
    private static final String PREFS_UI = "ui";
    private static final String PREF_DARK = "dark";

    private boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        darkMode = getSharedPreferences(PREFS_UI, MODE_PRIVATE).getBoolean(PREF_DARK, false);
        // 必须在 super.onCreate 前切换，深链模式启动瞬间才能匹配深浅色背景。
        setTheme(darkMode ? R.style.AppTheme_Dark : R.style.AppTheme);
        super.onCreate(savedInstanceState);

        Destination destination = destinationFromIntent();
        if (destination != null) {
            DeepLinkLauncher.open(this, destination);
            finish();
            return;
        }

        Theme theme = Theme.of(darkMode);
        applyEdgeToEdgeChrome(theme);
        setContentView(new HomeView(this, theme, this::toggleDarkMode).build());
    }

    /**
     * 小组件与固定快捷方式使用 {@link #ACTION_OPEN} + extra，
     * res/xml/shortcuts.xml 的静态快捷方式使用 {@link #ACTION_OPEN_PREFIX} + 平台 key。
     * 新增平台只需在 Destination 中登记，无需再维护 action 分支。
     */
    private Destination destinationFromIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_OPEN.equals(action)) {
            return Destination.fromKey(intent.getStringExtra(EXTRA_DESTINATION));
        }
        return Destination.fromAction(action, ACTION_OPEN_PREFIX);
    }

    /**
     * 状态栏与导航栏透明；API 30 起使用系统栏控制器，旧版本保留兼容分支。
     */
    @SuppressWarnings("deprecation")
    private void applyEdgeToEdgeChrome(Theme theme) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
            // setContentView 之前 PhoneWindow 尚未创建 DecorView，
            // 直接 getInsetsController() 会抛 NPE；先取 DecorView 再获取控制器。
            WindowInsetsController controller = window.getDecorView().getWindowInsetsController();
            if (controller != null) {
                int lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(theme.dark ? 0 : lightBars, lightBars);
            }
            return;
        }

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (!theme.dark) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        getSharedPreferences(PREFS_UI, MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_DARK, darkMode)
                .apply();
        recreate();
    }
}
