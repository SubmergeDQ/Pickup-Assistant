package com.pickup.tool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 主界面，复刻 docs/screenshots/01-main.png 的设计稿布局。
 *
 * 层次结构：
 * 1. 日期头部 —— “TODAY · OCT 8” 小字 + 右侧深浅色切换圆钮；
 * 2. 日期主视觉 —— 大号珊瑚色日期数字，右侧 PICKUP / 取件助手；
 * 3. 一句话文案 —— 星期 + 每日一句（按天轮换）；
 * 4. 周历条 —— 周一至周日，今天用珊瑚色胶囊高亮；
 * 5. 「打开身份码」—— 列表行：官方图标 + 标题 + 副标题 + 双箭头；
 * 6. 「查看待取」—— 同样的列表行；
 * 7. 「添加到桌面」—— 底部胶囊按钮，保留原有固定快捷方式功能；
 * 8. 免责说明与署名固定在屏幕底部。
 *
 * 背景为粉→米→淡绿的柔和纵向渐变，卡片为纯白圆角；支持深色模式。
 */
public final class MainActivity extends Activity {
    static final String ACTION_OPEN = "com.pickup.tool.OPEN";
    static final String EXTRA_DESTINATION = "destination";

    /** 深浅色偏好存储。 */
    private static final String PREFS_UI = "ui";
    private static final String PREF_DARK = "dark";

    /** M3 圆角令牌：卡片 20dp，胶囊 full。 */
    private static final float RADIUS_CARD_DP = 20f;
    private static final float RADIUS_PILL_DP = 23f;

    /** 每日一句，按天轮换。 */
    private static final String[] DAILY_QUOTES = {
            "北方有佳人，绝世而独立。",
            "长风破浪会有时，直挂云帆济沧海。",
            "采菊东篱下，悠然见南山。",
            "海内存知己，天涯若比邻。",
            "会当凌绝顶，一览众山小。",
            "落霞与孤鹜齐飞，秋水共长天一色。",
            "大鹏一日同风起，扶摇直上九万里。",
            "行到水穷处，坐看云起时。",
            "星垂平野阔，月涌大江流。",
            "春风得意马蹄疾，一日看尽长安花。"
    };

    /** 周历条的列标签（周一至周日）。 */
    private static final String[] WEEKDAY_LABELS = {
            "周一", "周二", "周三", "周四", "周五", "周六", "周日"
    };

    private static final String[] WEEKDAY_CHINESE = {
            "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
    };

    private float density;

    // ---- 当前主题调色板，onCreate 里按深浅色模式赋值 ----
    private int bgTop;
    private int bgMid;
    private int bgBottom;
    private int cardColor;
    private int textPrimary;
    private int textSecondary;
    private int accent;
    private int onAccent;

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

        density = getResources().getDisplayMetrics().density;
        darkMode = getSharedPreferences(PREFS_UI, MODE_PRIVATE).getBoolean(PREF_DARK, false);
        applyPalette();
        applyEdgeToEdgeChrome();
        setContentView(buildContent());
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

    /** 按深浅色模式取调色板。 */
    private void applyPalette() {
        if (darkMode) {
            bgTop = Color.rgb(0x24, 0x1F, 0x1C);
            bgMid = Color.rgb(0x26, 0x22, 0x20);
            bgBottom = Color.rgb(0x1F, 0x24, 0x20);
            cardColor = Color.rgb(0x33, 0x2E, 0x2A);
            textPrimary = Color.rgb(0xF2, 0xEC, 0xE6);
            textSecondary = Color.rgb(0xAB, 0xA1, 0x98);
            accent = Color.rgb(0xF0, 0x92, 0x7F);
            onAccent = Color.rgb(0x2A, 0x14, 0x10);
        } else {
            // 实测设计稿：左上粉 (242,217,210)、中部奶油白 (243,242,237)、右下淡紫 (232,227,234)
            bgTop = Color.rgb(0xF2, 0xD9, 0xD2);
            bgMid = Color.rgb(0xF4, 0xF2, 0xED);
            bgBottom = Color.rgb(0xE9, 0xE4, 0xEA);
            cardColor = Color.rgb(0xFF, 0xFF, 0xFF);
            textPrimary = Color.rgb(0x20, 0x16, 0x12);
            textSecondary = Color.rgb(0x8B, 0x81, 0x78);
            accent = Color.rgb(0xE9, 0x7B, 0x6F);
            onAccent = Color.rgb(0xFF, 0xF6, 0xF2);
        }
    }

    /**
     * 状态栏与导航栏透明；浅色用深色系统栏图标，深色反之。
     */
    private void applyEdgeToEdgeChrome() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (!darkMode) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private View buildContent() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(backgroundGradient());

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setClipToPadding(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        content.addView(buildHeader());
        content.addView(buildDateHero());
        content.addView(buildQuoteLine());
        content.addView(buildWeekStrip());

        content.addView(sectionHeader("打开身份码", 3));
        content.addView(entryRow(Destination.CAINIAO));
        content.addView(entryRow(Destination.TAOBAO));
        content.addView(entryRow(Destination.PINDUODUO));

        content.addView(sectionHeader("查看待取", 6));
        content.addView(entryRow(Destination.TAOBAO_PENDING));
        content.addView(entryRow(Destination.PINDUODUO_PENDING));
        content.addView(entryRow(Destination.JD));
        content.addView(entryRow(Destination.XHS));
        content.addView(entryRow(Destination.MEITUAN));
        content.addView(entryRow(Destination.DOUYIN));

        content.addView(sectionHeader("添加到桌面", 0, "需要打开桌面快捷方式权限"));
        content.addView(buildPinRow());

        content.addView(buildDisclaimer());

        applyInsets(root, content);
        return root;
    }

    /** 页面背景：设计稿是左上粉 → 中间奶油白 → 右下淡紫的斜向柔和渐变。 */
    private GradientDrawable backgroundGradient() {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {bgTop, bgMid, bgBottom}
        );
        return gradient;
    }

    /**
     * 日期头部：TODAY · OCT 8 小字 + 右侧深浅色切换圆钮。
     */
    private View buildHeader() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(0, dp(6), 0, dp(10));

        TextView today = text(todayLabel(), 12, textSecondary, Typeface.BOLD);
        today.setLetterSpacing(0.14f);
        today.setAllCaps(true);
        bar.addView(today, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // 设计稿的月亮是深色新月字形（浅色模式）；深色模式用太阳字形。用单色字形而非 emoji。
        TextView moon = text(darkMode ? "☀" : "☾", 19, textPrimary, Typeface.BOLD);
        moon.setGravity(Gravity.CENTER);
        moon.setBackground(M3.tonal(density, 20f, cardColor));
        moon.setClickable(true);
        moon.setFocusable(true);
        moon.setContentDescription(darkMode ? "切换到浅色模式" : "切换到深色模式");
        moon.setOnClickListener(view -> toggleDarkMode());
        bar.addView(moon, new LinearLayout.LayoutParams(dp(38), dp(38)));

        return bar;
    }

    private String todayLabel() {
        String formatted = new SimpleDateFormat("MMM d", Locale.US).format(Calendar.getInstance().getTime());
        return "TODAY · " + formatted.toUpperCase(Locale.ROOT);
    }

    /** 英文序数后缀：1st / 2nd / 3rd / 4th…，11~13 例外统一用 th。 */
    static String ordinalSuffix(int day) {
        return Dates.ordinalSuffix(day);
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        getSharedPreferences(PREFS_UI, MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_DARK, darkMode)
                .apply();
        recreate();
    }

    /**
     * 日期主视觉：大号珊瑚色日期数字 + 月份小字，右侧 PICKUP / 取件助手。
     */
    private View buildDateHero() {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.BOTTOM);
        // 内容区留白 26dp + 周历条外边距 6dp = 32dp：让数字左缘与取件助手右缘和周历条容器边缘对齐。
        // 左侧再收 3dp 抵消数字字形的侧留白，使字形左缘与周历条容器左缘重合。
        hero.setPadding(dp(3), dp(2), dp(6), dp(2));

        Calendar calendar = Calendar.getInstance();

        // 设计稿：超大超粗的珊瑚色日期数字，右下角带序数后缀（8th / 1st / 2nd / 3rd）。
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day = String.valueOf(dayOfMonth);
        int dateColor = darkMode ? accent : Color.rgb(0xE5, 0x8E, 0x91);
        SpannableString dateText = new SpannableString(day + ordinalSuffix(dayOfMonth));
        dateText.setSpan(new RelativeSizeSpan(0.28f), day.length(), dateText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dateText.setSpan(new StyleSpan(Typeface.BOLD), day.length(), dateText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView dateBlock = text("", 76, dateColor, Typeface.BOLD);
        dateBlock.setText(dateText);
        dateBlock.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        dateBlock.setIncludeFontPadding(false);
        hero.addView(dateBlock, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // 设计稿中标题块贴右侧：数字居左，中间用弹性占位把标题推到右边。
        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setGravity(Gravity.END);

        TextView pickup = text("PICKUP", 11, textSecondary, Typeface.BOLD);
        pickup.setLetterSpacing(0.18f);
        pickup.setAllCaps(true);
        titles.addView(pickup);

        TextView appName = text("取件助手", 21, textPrimary, Typeface.BOLD);
        LinearLayout.LayoutParams appParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        appParams.topMargin = dp(2);
        appName.setLayoutParams(appParams);
        titles.addView(appName);

        hero.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1f));
        hero.addView(titles, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        return hero;
    }

    /** 星期 + 每日一句。 */
    private View buildQuoteLine() {
        Calendar calendar = Calendar.getInstance();
        String weekday = WEEKDAY_CHINESE[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        String quote = DAILY_QUOTES[calendar.get(Calendar.DAY_OF_YEAR) % DAILY_QUOTES.length];

        TextView line = text(weekday + "·" + quote, 13, textSecondary, Typeface.NORMAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(2);
        params.bottomMargin = dp(14);
        line.setLayoutParams(params);
        return line;
    }

    /**
     * 周历条：周一至周日，今天用珊瑚色胶囊高亮并带圆点标记。
     */
    private View buildWeekStrip() {
        LinearLayout strip = new LinearLayout(this);
        strip.setOrientation(LinearLayout.HORIZONTAL);
        strip.setPadding(dp(10), dp(12), dp(10), dp(10));
        strip.setBackground(M3.tonal(density, RADIUS_CARD_DP, cardColor));

        Calendar calendar = Calendar.getInstance();
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        // 周一为一周起点：Calendar 星期日=1 … 星期六=7，偏移 (dow + 5) % 7。
        int mondayOffset = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -mondayOffset);

        for (int index = 0; index < 7; index++) {
            boolean isToday = calendar.get(Calendar.DAY_OF_MONTH) == todayDay;

            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.CENTER_HORIZONTAL);

            // 标签占满整列、文字居中：不依赖容器 gravity，保证与下方数字严格对中。
            TextView label = text(WEEKDAY_LABELS[index], 11, textSecondary, Typeface.BOLD);
            label.setGravity(Gravity.CENTER_HORIZONTAL);
            label.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            column.addView(label);

            TextView number = text(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 15,
                    isToday ? onAccent : textPrimary, Typeface.BOLD);
            number.setGravity(Gravity.CENTER);
            if (isToday) {
                number.setBackground(M3.tonal(density, 17f, accent));
            }
            LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(dp(32), dp(32));
            numberParams.gravity = Gravity.CENTER_HORIZONTAL;
            column.addView(number, numberParams);

            // 圆点标记：今天下方有珊瑚色圆点，其余列为透明占位，保持对齐。
            View dot = new View(this);
            dot.setBackground(M3.tonal(density, 2f, isToday ? accent : Color.TRANSPARENT));
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(4), dp(4));
            dotParams.topMargin = dp(3);
            dotParams.gravity = Gravity.CENTER_HORIZONTAL;
            dot.setLayoutParams(dotParams);
            column.addView(dot);

            strip.addView(column, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginStart(dp(6));
        params.setMarginEnd(dp(6));
        params.bottomMargin = dp(20);
        strip.setLayoutParams(params);
        return strip;
    }

    /**
     * 小节标题：珊瑚色竖条 + 标题 + 平台数量（可选附注）。
     */
    private View sectionHeader(String title, int platformCount) {
        return sectionHeader(title, platformCount, null);
    }

    private View sectionHeader(String title, int platformCount, String note) {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, dp(2), 0, dp(10));

        View bar = new View(this);
        bar.setBackground(M3.tonal(density, 1.5f, accent));
        header.addView(bar, new LinearLayout.LayoutParams(dp(3), dp(13)));

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.leftMargin = dp(8);
        TextView titleView = text(title, 15, textPrimary, Typeface.BOLD);
        titleView.setLayoutParams(titleParams);
        header.addView(titleView);

        if (platformCount > 0) {
            LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            countParams.leftMargin = dp(7);
            TextView countView = text(platformCount + "平台", 11, textSecondary, Typeface.NORMAL);
            countView.setLayoutParams(countParams);
            header.addView(countView);
        }

        if (note != null && !note.isEmpty()) {
            LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            noteParams.leftMargin = dp(6);
            TextView noteView = text("（" + note + "）", 10, textSecondary, Typeface.NORMAL);
            noteView.setLayoutParams(noteParams);
            header.addView(noteView);
        }

        return header;
    }

    /**
     * 入口列表行：官方图标 + 标题 + 副标题 + 双箭头，白卡圆角。
     */
    private View entryRow(Destination destination) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        row.setMinimumHeight(dp(64));
        row.setClickable(true);
        row.setFocusable(true);
        row.setContentDescription("打开" + destination.title);
        row.setOnClickListener(view -> DeepLinkLauncher.open(this, destination));
        row.setBackground(M3.surface(density, RADIUS_CARD_DP, cardColor, 0f, 0f));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // 设计稿里卡片比正文再内缩一点（约 8% 屏宽），避免贴边。
        params.setMarginStart(dp(6));
        params.setMarginEnd(dp(6));
        params.bottomMargin = dp(10);
        row.setLayoutParams(params);

        View badge = entryBadge(destination, onAccent, accent, 40, RADIUS_CARD_DP);
        row.addView(badge, new LinearLayout.LayoutParams(dp(40), dp(40)));

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(12), 0, 0, 0);

        TextView titleView = text(destination.label, 15, textPrimary, Typeface.BOLD);
        titleView.setSingleLine(true);
        labels.addView(titleView);

        TextView subtitleView = text(destination.subtitle, 11, textSecondary, Typeface.NORMAL);
        subtitleView.setSingleLine(true);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = dp(3);
        subtitleView.setLayoutParams(subtitleParams);
        labels.addView(subtitleView);

        row.addView(labels, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView chevron = text("»", 18, textSecondary, Typeface.BOLD);
        chevron.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        row.addView(chevron);

        return row;
    }

    /**
     * 入口图标：有官方图标的平台直接显示官方图标，否则回退为文字徽章。
     */
    private View entryBadge(
            Destination destination,
            int onContainerColor,
            int containerColor,
            int sizeDp,
            float radiusDp
    ) {
        int iconRes = brandIconRes(destination);
        if (iconRes != 0) {
            ImageView icon = new ImageView(this);
            icon.setImageResource(iconRes);
            icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            // 官方图标自带圆角与底色，铺满徽章位即可。
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setContentDescription(destination.title);
            return icon;
        }

        TextView badge = text(destination.mark, 19, onContainerColor, Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(M3.tonal(density, radiusDp, containerColor));
        return badge;
    }

    /**
     * 各平台官方图标。
     */
    private int brandIconRes(Destination destination) {
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
                return 0;
        }
    }

    /**
     * 内容流末尾的免责说明。
     */
    private View buildDisclaimer() {
        TextView note = text(
                "第三方应用更新后，内部页面地址可能发生变化。",
                12,
                textSecondary,
                Typeface.NORMAL
        );
        note.setLineSpacing(0f, 1.4f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(18);
        params.bottomMargin = dp(8);
        note.setLayoutParams(params);
        return note;
    }

    /**
     * 底部固定快捷方式：胶囊按钮，三个一行。
     */
    private View buildPinRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(3f);

        addPinButton(row, Destination.CAINIAO, "菜鸟", "身份码", M3.CAINIAO_ACCENT);
        addPinButton(row, Destination.TAOBAO, "淘宝", "取件码", M3.TAOBAO_ACCENT);
        addPinButton(row, Destination.PINDUODUO, "拼多多", "身份码", M3.PINDUODUO_ACCENT);
        return row;
    }

    private void addPinButton(
            LinearLayout parent,
            Destination destination,
            String appName,
            String codeType,
            int accentColor
    ) {
        LinearLayout button = new LinearLayout(this);
        button.setOrientation(LinearLayout.HORIZONTAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(10), dp(12), dp(10), dp(12));
        button.setMinimumHeight(dp(46));
        button.setClickable(true);
        button.setFocusable(true);
        button.setContentDescription("将" + destination.title + "添加到桌面");
        button.setOnClickListener(view -> ShortcutPinning.request(this, destination));
        button.setBackground(M3.surface(density, RADIUS_PILL_DP, cardColor, 0f, 0f));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        if (parent.getChildCount() > 0) {
            params.leftMargin = dp(8);
        }
        button.setLayoutParams(params);

        View dot = new View(this);
        dot.setBackground(M3.tonal(density, 6f, accentColor));
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(12), dp(12));
        dotParams.rightMargin = dp(8);
        button.addView(dot, dotParams);

        // 按钮文案 = 平台名 + 括号标注的码类型，平台名用品牌色以便一眼区分。
        SpannableString label = new SpannableString(appName + "（" + codeType + "）");
        label.setSpan(
                new ForegroundColorSpan(accentColor),
                0,
                appName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        label.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                appName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        int openParen = appName.length();
        label.setSpan(
                new RelativeSizeSpan(0.86f),
                openParen,
                label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        label.setSpan(
                new ForegroundColorSpan(textSecondary),
                openParen,
                label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        TextView labelView = text("", 13, textPrimary, Typeface.BOLD);
        labelView.setText(label);
        labelView.setSingleLine(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            labelView.setAutoSizeTextTypeUniformWithConfiguration(
                    9,
                    12,
                    1,
                    TypedValue.COMPLEX_UNIT_SP
            );
        }
        // 自动缩放之外再兜底：文字排不下时优先省略平台名之后的括注，而不是整段被裁掉。
        labelView.setEllipsize(null);
        labelView.setHorizontallyScrolling(false);
        button.addView(labelView);

        parent.addView(button);
    }

    /**
     * 内容避开状态栏、导航栏与刘海；底部署名栏单独承担导航栏内边距。
     */
    private void applyInsets(View root, final LinearLayout content) {
        final int baseTop = dp(12);
        final int contentBottom = dp(18);
        final int horizontal = dp(26);
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            content.setPadding(
                    horizontal + insets.getSystemWindowInsetLeft(),
                    baseTop + insets.getSystemWindowInsetTop(),
                    horizontal + insets.getSystemWindowInsetRight(),
                    contentBottom + insets.getSystemWindowInsetBottom()
            );
            return insets;
        });
        root.requestApplyInsets();
    }

    private TextView text(String value, float sizeSp, int color, int style) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        view.setTextColor(color);
        // 中文用系统默认无衬线，字重通过 bold 表达 label / title 层级。
        view.setTypeface(Typeface.create("sans-serif", style));
        view.setIncludeFontPadding(false);
        return view;
    }

    private int dp(float value) {
        return Math.round(value * density);
    }
}
