package com.pickup.tool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Toast;

final class ShortcutPinning {
    private ShortcutPinning() {
    }

    static void request(Context context, Destination destination) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(context, "当前系统不支持固定快捷入口", Toast.LENGTH_SHORT).show();
            return;
        }

        ShortcutManager manager = context.getSystemService(ShortcutManager.class);
        if (manager == null || !manager.isRequestPinShortcutSupported()) {
            Toast.makeText(context, "当前桌面不支持固定快捷入口", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent launchIntent = new Intent(context, MainActivity.class)
                .setAction(MainActivity.ACTION_OPEN)
                .putExtra(MainActivity.EXTRA_DESTINATION, destination.key);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(
                context,
                "pinned_" + destination.key
        )
                .setShortLabel(destination.title)
                .setLongLabel("打开" + destination.title)
                .setIcon(Icon.createWithResource(context, iconFor(destination)))
                .setIntent(launchIntent)
                .build();

        manager.requestPinShortcut(shortcut, null);
    }

    private static int iconFor(Destination destination) {
        switch (destination) {
            case CAINIAO:
                return R.drawable.ic_brand_cainiao;
            case TAOBAO:
            case TAOBAO_PENDING:
                return R.drawable.ic_brand_taobao;
            case PINDUODUO:
            case PINDUODUO_PENDING:
                return R.drawable.ic_brand_pinduoduo;
            case JD:
                return R.drawable.ic_brand_jd;
            case XHS:
                return R.drawable.ic_brand_xhs;
            case MEITUAN:
                return R.drawable.ic_brand_meituan;
            case DOUYIN:
                return R.drawable.ic_brand_douyin;
            default:
                // 新增枚举若忘记映射图标，用应用图标兜底，避免误用其它品牌图标。
                return R.mipmap.ic_launcher;
        }
    }
}
