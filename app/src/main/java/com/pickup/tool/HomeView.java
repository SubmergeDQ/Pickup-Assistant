package com.pickup.tool;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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
 * 首页视图，按设计稿构建：
 * 1. 日期头部 —— “TODAY · OCT 8” 小字 + 右侧深浅色切换圆钮；
 * 2. 日期主视觉 —— 大号珊瑚色日期数字，右侧 PICKUP / 取件助手；
 * 3. 一句话文案 —— 星期 + 每日一句（按天轮换）；
 * 4. 周历条 —— 周一至周日，今天用珊瑚色胶囊高亮；
 * 5. 「打开身份码」「查看待取」入口列表；
 * 6. 「添加到桌面」胶囊按钮；
 * 7. 免责说明。
 */
final class HomeView {
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

    private final Activity activity;
    private final Theme theme;
    private final Runnable onToggleDarkMode;
    private final float density;

    HomeView(Activity activity, Theme theme, Runnable onToggleDarkMode) {
        this.activity = activity;
        this.theme = theme;
        this.onToggleDarkMode = onToggleDarkMode;
        this.density = activity.getResources().getDisplayMetrics().density;
    }

    View build() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(backgroundGradient());

        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setClipToPadding(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout content = new LinearLayout(activity);
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
        return new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {theme.bgTop, theme.bgMid, theme.bgBottom}
        );
    }

    /**
     * 日期头部：TODAY · OCT 8 小字 + 右侧深浅色切换圆钮。
     */
    private View buildHeader() {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(0, dp(6), 0, dp(10));

        TextView today = text(todayLabel(), 12, theme.textSecondary, Typeface.BOLD);
        today.setLetterSpacing(0.14f);
        today.setAllCaps(true);
        bar.addView(today, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // 设计稿的月亮是深色新月字形（浅色模式）；深色模式用太阳字形。用单色字形而非 emoji。
        TextView moon = text(theme.dark ? "☀" : "☾", 19, theme.textPrimary, Typeface.BOLD);
        moon.setGravity(Gravity.CENTER);
        moon.setBackground(M3.tonal(density, 20f, theme.cardColor));
        moon.setClickable(true);
        moon.setFocusable(true);
        moon.setContentDescription(theme.dark ? "切换到浅色模式" : "切换到深色模式");
        moon.setOnClickListener(view -> onToggleDarkMode.run());
        bar.addView(moon, new LinearLayout.LayoutParams(dp(38), dp(38)));

        return bar;
    }

    private String todayLabel() {
        String formatted = new SimpleDateFormat("MMM d", Locale.US)
                .format(Calendar.getInstance().getTime());
        return "TODAY · " + formatted.toUpperCase(Locale.ROOT);
    }

    /**
     * 日期主视觉：大号珊瑚色日期数字 + 月份小字，右侧 PICKUP / 取件助手。
     */
    private View buildDateHero() {
        LinearLayout hero = new LinearLayout(activity);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.BOTTOM);
        // 内容区留白 26dp + 周历条外边距 6dp = 32dp：让数字左缘与取件助手右缘和周历条容器边缘对齐。
        // 左侧再收 3dp 抵消数字字形的侧留白，使字形左缘与周历条容器左缘重合。
        hero.setPadding(dp(3), dp(2), dp(6), dp(2));

        Calendar calendar = Calendar.getInstance();

        // 设计稿：超大超粗的珊瑚色日期数字，右下角带序数后缀（8th / 1st / 2nd / 3rd）。
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day = String.valueOf(dayOfMonth);
        int dateColor = theme.dark ? theme.accent : Color.rgb(0xE5, 0x8E, 0x91);
        SpannableString dateText = new SpannableString(day + Dates.ordinalSuffix(dayOfMonth));
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
        LinearLayout titles = new LinearLayout(activity);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setGravity(Gravity.END);

        TextView pickup = text("PICKUP", 11, theme.textSecondary, Typeface.BOLD);
        pickup.setLetterSpacing(0.18f);
        pickup.setAllCaps(true);
        titles.addView(pickup);

        TextView appName = text("取件助手", 21, theme.textPrimary, Typeface.BOLD);
        LinearLayout.LayoutParams appParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        appParams.topMargin = dp(2);
        appName.setLayoutParams(appParams);
        titles.addView(appName);

        hero.addView(new View(activity), new LinearLayout.LayoutParams(0, 1, 1f));
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

        TextView line = text(weekday + "·" + quote, 13, theme.textSecondary, Typeface.NORMAL);
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
        LinearLayout strip = new LinearLayout(activity);
        strip.setOrientation(LinearLayout.HORIZONTAL);
        strip.setPadding(dp(10), dp(12), dp(10), dp(10));
        strip.setBackground(M3.tonal(density, RADIUS_CARD_DP, theme.cardColor));

        Calendar calendar = Calendar.getInstance();
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        // 周一为一周起点：Calendar 星期日=1 … 星期六=7，偏移 (dow + 5) % 7。
        int mondayOffset = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -mondayOffset);

        for (int index = 0; index < 7; index++) {
            boolean isToday = calendar.get(Calendar.DAY_OF_MONTH) == todayDay;

            LinearLayout column = new LinearLayout(activity);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.CENTER_HORIZONTAL);

            // 标签占满整列、文字居中：不依赖容器 gravity，保证与下方数字严格对中。
            TextView label = text(WEEKDAY_LABELS[index], 11, theme.textSecondary, Typeface.BOLD);
            label.setGravity(Gravity.CENTER_HORIZONTAL);
            label.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            column.addView(label);

            TextView number = text(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 15,
                    isToday ? theme.onAccent : theme.textPrimary, Typeface.BOLD);
            number.setGravity(Gravity.CENTER);
            if (isToday) {
                number.setBackground(M3.tonal(density, 17f, theme.accent));
            }
            LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(dp(32), dp(32));
            numberParams.gravity = Gravity.CENTER_HORIZONTAL;
            column.addView(number, numberParams);

            // 圆点标记：今天下方有珊瑚色圆点，其余列为透明占位，保持对齐。
            View dot = new View(activity);
            dot.setBackground(M3.tonal(density, 2f, isToday ? theme.accent : Color.TRANSPARENT));
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(4), dp(4));
            dotParams.topMargin = dp(3);
            dotParams.gravity = Gravity.CENTER_HORIZONTAL;
            dot.setLayoutParams(dotParams);
            column.addView(dot);

            strip.addView(column,
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
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
        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, dp(2), 0, dp(10));

        View bar = new View(activity);
        bar.setBackground(M3.tonal(density, 1.5f, theme.accent));
        header.addView(bar, new LinearLayout.LayoutParams(dp(3), dp(13)));

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.leftMargin = dp(8);
        TextView titleView = text(title, 15, theme.textPrimary, Typeface.BOLD);
        titleView.setLayoutParams(titleParams);
        header.addView(titleView);

        if (platformCount > 0) {
            LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            countParams.leftMargin = dp(7);
            TextView countView = text(platformCount + "平台", 11, theme.textSecondary,
                    Typeface.NORMAL);
            countView.setLayoutParams(countParams);
            header.addView(countView);
        }

        if (note != null && !note.isEmpty()) {
            LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            noteParams.leftMargin = dp(6);
            TextView noteView = text("（" + note + "）", 10, theme.textSecondary, Typeface.NORMAL);
            noteView.setLayoutParams(noteParams);
            header.addView(noteView);
        }

        return header;
    }

    /**
     * 入口列表行：官方图标 + 标题 + 副标题 + 双箭头，白卡圆角。
     */
    private View entryRow(Destination destination) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(12), dp(14), dp(12));
        row.setMinimumHeight(dp(64));
        row.setClickable(true);
        row.setFocusable(true);
        row.setContentDescription("打开" + destination.title);
        row.setOnClickListener(view -> DeepLinkLauncher.open(activity, destination));
        row.setBackground(M3.surface(density, RADIUS_CARD_DP, theme.cardColor, 0f, 0f));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // 设计稿里卡片比正文再内缩一点（约 8% 屏宽），避免贴边。
        params.setMarginStart(dp(6));
        params.setMarginEnd(dp(6));
        params.bottomMargin = dp(10);
        row.setLayoutParams(params);

        View badge = entryBadge(destination, theme.onAccent, theme.accent, 40, RADIUS_CARD_DP);
        row.addView(badge, new LinearLayout.LayoutParams(dp(40), dp(40)));

        LinearLayout labels = new LinearLayout(activity);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(12), 0, 0, 0);

        TextView titleView = text(destination.label, 15, theme.textPrimary, Typeface.BOLD);
        titleView.setSingleLine(true);
        labels.addView(titleView);

        TextView subtitleView = text(destination.subtitle, 11, theme.textSecondary,
                Typeface.NORMAL);
        subtitleView.setSingleLine(true);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = dp(3);
        subtitleView.setLayoutParams(subtitleParams);
        labels.addView(subtitleView);

        row.addView(labels, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView chevron = text("»", 18, theme.textSecondary, Typeface.BOLD);
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
            ImageView icon = new ImageView(activity);
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
                theme.textSecondary,
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
        LinearLayout row = new LinearLayout(activity);
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
        LinearLayout button = new LinearLayout(activity);
        button.setOrientation(LinearLayout.HORIZONTAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(10), dp(12), dp(10), dp(12));
        button.setMinimumHeight(dp(46));
        button.setClickable(true);
        button.setFocusable(true);
        button.setContentDescription("将" + destination.title + "添加到桌面");
        button.setOnClickListener(view -> ShortcutPinning.request(activity, destination));
        button.setBackground(M3.surface(density, RADIUS_PILL_DP, theme.cardColor, 0f, 0f));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        if (parent.getChildCount() > 0) {
            params.leftMargin = dp(8);
        }
        button.setLayoutParams(params);

        View dot = new View(activity);
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
                new ForegroundColorSpan(theme.textSecondary),
                openParen,
                label.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        TextView labelView = text("", 13, theme.textPrimary, Typeface.BOLD);
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
        TextView view = new TextView(activity);
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
