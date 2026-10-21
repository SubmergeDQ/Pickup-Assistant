package com.pickup.tool;

import android.graphics.Color;

/**
 * 深浅色主题调色板。
 */
final class Theme {
    final boolean dark;
    final int bgTop;
    final int bgMid;
    final int bgBottom;
    final int cardColor;
    final int textPrimary;
    final int textSecondary;
    final int accent;
    final int onAccent;

    private Theme(
            boolean dark,
            int bgTop,
            int bgMid,
            int bgBottom,
            int cardColor,
            int textPrimary,
            int textSecondary,
            int accent,
            int onAccent
    ) {
        this.dark = dark;
        this.bgTop = bgTop;
        this.bgMid = bgMid;
        this.bgBottom = bgBottom;
        this.cardColor = cardColor;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.accent = accent;
        this.onAccent = onAccent;
    }

    static Theme of(boolean dark) {
        if (dark) {
            return new Theme(
                    true,
                    Color.rgb(0x24, 0x1F, 0x1C),
                    Color.rgb(0x26, 0x22, 0x20),
                    Color.rgb(0x1F, 0x24, 0x20),
                    Color.rgb(0x33, 0x2E, 0x2A),
                    Color.rgb(0xF2, 0xEC, 0xE6),
                    Color.rgb(0xAB, 0xA1, 0x98),
                    Color.rgb(0xF0, 0x92, 0x7F),
                    Color.rgb(0x2A, 0x14, 0x10)
            );
        }
        // 实测设计稿：左上粉 (242,217,210)、中部奶油白 (243,242,237)、右下淡紫 (232,227,234)
        return new Theme(
                false,
                Color.rgb(0xF2, 0xD9, 0xD2),
                Color.rgb(0xF4, 0xF2, 0xED),
                Color.rgb(0xE9, 0xE4, 0xEA),
                Color.rgb(0xFF, 0xFF, 0xFF),
                Color.rgb(0x20, 0x16, 0x12),
                Color.rgb(0x8B, 0x81, 0x78),
                Color.rgb(0xE9, 0x7B, 0x6F),
                Color.rgb(0xFF, 0xF6, 0xF2)
        );
    }
}
