package com.pickup.tool;

/**
 * 日期展示相关的纯函数，便于单元测试。
 */
final class Dates {
    private Dates() {
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
