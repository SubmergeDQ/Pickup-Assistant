package com.pickup.tool;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

/**
 * 主界面入口：解析深链跳转请求，维护深浅色偏好，界面构建交给 {@link HomeView}。
 */
public final class MainActivity extends Activity {
    static final String ACTION_OPEN = "com.pickup.tool.OPEN";
    static final String EXTRA_DESTINATION = "destination";

    /** 深浅色偏好存储。 */
    private static final String PREFS_UI = "ui";
    private static final String PREF_DARK = "dark";

    private boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Destination destination = destinationFromIntent();
        if (destination != null) {
            DeepLinkLauncher.open(this, destination);
            finish();
            return;
        }

        darkMode = getSharedPreferences(PREFS_UI, MODE_PRIVATE).getBoolean(PREF_DARK, false);
        Theme theme = Theme.of(darkMode);
        applyEdgeToEdgeChrome(theme);
        setContentView(new HomeView(this, theme, this::toggleDarkMode).build());
    }

    private Destination destinationFromIntent() {
        String action = getIntent().getAction();
        if (ACTION_OPEN.equals(action)) {
            return Destination.fromKey(getIntent().getStringExtra(EXTRA_DESTINATION));
        }
        if ("com.pickup.tool.OPEN_CAINIAO".equals(action)) {
            return Destination.CAINIAO;
        }
        if ("com.pickup.tool.OPEN_TAOBAO".equals(action)) {
            return Destination.TAOBAO;
        }
        if ("com.pickup.tool.OPEN_PINDUODUO".equals(action)) {
            return Destination.PINDUODUO;
        }
        if ("com.pickup.tool.OPEN_TAOBAO_PENDING".equals(action)) {
            return Destination.TAOBAO_PENDING;
        }
        if ("com.pickup.tool.OPEN_PINDUODUO_PENDING".equals(action)) {
            return Destination.PINDUODUO_PENDING;
        }
        if ("com.pickup.tool.OPEN_JD".equals(action)) {
            return Destination.JD;
        }
        if ("com.pickup.tool.OPEN_XHS".equals(action)) {
            return Destination.XHS;
        }
        if ("com.pickup.tool.OPEN_MEITUAN".equals(action)) {
            return Destination.MEITUAN;
        }
        if ("com.pickup.tool.OPEN_DOUYIN".equals(action)) {
            return Destination.DOUYIN;
        }
        return null;
    }

    /**
     * 状态栏与导航栏透明；浅色用深色系统栏图标，深色反之。
     */
    private void applyEdgeToEdgeChrome(Theme theme) {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (!theme.dark) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);
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
