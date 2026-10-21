package com.pickup.tool;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Material 3 表面与状态层 Drawable 工厂，以及首页使用的平台品牌强调色。
 *
 * 页面底色由 {@link Theme} 的渐变提供；这里只负责卡片按下态的色调混合、
 * 纯色容器和少量品牌色，避免 UI 代码直接拼装 Drawable。
 */
final class M3 {
    private M3() {
    }

    /** 状态层基色：与底色按比例混合，得到 M3 的按下态。 */
    private static final int ON_SURFACE = Color.rgb(0x23, 0x1A, 0x15);
    /** 按下时的状态层，数值取 onSurface 的 8% 混合。 */
    private static final float STATE_LAYER = 0.08f;

    // ---- 首页底部固定快捷方式使用的品牌强调色 ----
    static final int CAINIAO_ACCENT = Color.rgb(0x14, 0x6C, 0x43);
    static final int TAOBAO_ACCENT = Color.rgb(0x8A, 0x4A, 0x00);
    static final int PINDUODUO_ACCENT = Color.rgb(0x93, 0x00, 0x0A);

    /**
     * 表面卡片：普通态与按下态各一层，按下时叠加状态层颜色。
     */
    static Drawable surface(
            float density,
            float radiusDp,
            int fillColor,
            float shadowDp,
            float shadowAlpha
    ) {
        StateListDrawable states = new StateListDrawable();
        states.setEnterFadeDuration(0);
        states.setExitFadeDuration(120);
        states.addState(
                new int[] {android.R.attr.state_pressed},
                new SurfaceDrawable(
                        density,
                        radiusDp,
                        layer(fillColor),
                        0f,
                        0f,
                        0f,
                        0
                )
        );
        states.addState(
                new int[0],
                new SurfaceDrawable(
                        density,
                        radiusDp,
                        fillColor,
                        shadowDp,
                        shadowAlpha,
                        0f,
                        0
                )
        );
        return states;
    }

    /**
     * 纯色调容器，用于图标底衬这类不需要交互的元素。
     */
    static Drawable tonal(float density, float radiusDp, int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(color);
        shape.setCornerRadius(radiusDp * density);
        return shape;
    }

    /**
     * 在底色上叠加一层 onSurface，得到 M3 的按下状态色。
     */
    private static int layer(int base) {
        return blend(base, ON_SURFACE, STATE_LAYER);
    }

    private static int blend(int base, int overlay, float ratio) {
        return Color.rgb(
                Math.round(Color.red(base) * (1f - ratio) + Color.red(overlay) * ratio),
                Math.round(Color.green(base) * (1f - ratio) + Color.green(overlay) * ratio),
                Math.round(Color.blue(base) * (1f - ratio) + Color.blue(overlay) * ratio)
        );
    }
}
