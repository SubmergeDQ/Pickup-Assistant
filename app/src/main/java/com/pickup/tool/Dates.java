package com.pickup.tool;

/**
 * 日期展示相关的纯函数，便于单元测试。
 */
final class Dates {
    private Dates() {
    }

    /**
     * 周一为一周起点时，距本周一的天数偏移。
     * Calendar 的 DAY_OF_WEEK 约定：星期日=1、星期一=2 … 星期六=7。
     */
    static int mondayOffset(int dayOfWeek) {
        return (dayOfWeek + 5) % 7;
    }

    /** 每日一句按一年中的第几天轮换。 */
    static int dailyQuoteIndex(int dayOfYear, int quoteCount) {
        return dayOfYear % quoteCount;
    }

    /** 英文序数后缀：1st / 2nd / 3rd / 4th…，11~13 例外统一用 th。 */
    static String ordinalSuffix(int day) {
        if (day % 100 >= 11 && day % 100 <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
