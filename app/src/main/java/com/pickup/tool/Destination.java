package com.pickup.tool;

import java.util.Locale;

enum Destination {
    CAINIAO(
            "cainiao",
            "菜鸟身份码",
            "菜鸟驿站取件",
            "优先打开身份码",
            "菜",
            "com.cainiao.wireless",
            new String[] {
                    "cainiao://desktop/station_code"
            },
            "https://market.m.taobao.com/app/cn-yz/multi-activity/authCode.html?bizEntry=ALIPAY_GUOGUO",
            true
    ),
    TAOBAO(
            "taobao",
            "淘宝取件码",
            "淘宝取件",
            "打开取件码页面",
            "淘",
            "com.taobao.taobao",
            new String[] {
                    "taobao://m.taobao.com/tbopen/index.html?h5Url=https%3A%2F%2Fpages-fast.m.taobao.com%2Fwow%2Fz%2Funiapp%2F1011717%2Flast-mile-fe%2Fend-collect-platform%2Fidentity-code%3Fx-ssr%3Dtrue",
                    "tbopen://m.taobao.com/tbopen/index.html?h5Url=https%3A%2F%2Fpages-fast.m.taobao.com%2Fwow%2Fz%2Funiapp%2F1011717%2Flast-mile-fe%2Fend-collect-platform%2Fidentity-code%3Fx-ssr%3Dtrue"
            },
            "https://pages-fast.m.taobao.com/wow/z/uniapp/1011717/last-mile-fe/end-collect-platform/identity-code?x-ssr=true"
    ),
    TAOBAO_PENDING(
            "taobao_pending",
            "淘宝待取快递",
            "淘宝待取快递",
            "末端驿站待取列表",
            "淘",
            "com.taobao.taobao",
            new String[] {
                    "taobao://m.taobao.com/tbopen/index.html?h5Url=https%3A%2F%2Fpages-fast.m.taobao.com%2Fwow%2Fz%2Funiapp%2F1100333%2Flast-mile-fe%2Fm-end-school-tab%2Fhome",
                    "tbopen://m.taobao.com/tbopen/index.html?h5Url=https%3A%2F%2Fpages-fast.m.taobao.com%2Fwow%2Fz%2Funiapp%2F1100333%2Flast-mile-fe%2Fm-end-school-tab%2Fhome"
            },
            "https://pages-fast.m.taobao.com/wow/z/uniapp/1100333/last-mile-fe/m-end-school-tab/home"
    ),
    PINDUODUO(
            "pinduoduo",
            "拼多多身份码",
            "拼多多取件",
            "打开拼多多裹裹身份码",
            "拼",
            "com.xunmeng.pinduoduo",
            new String[] {
                    "pinduoduo://com.xunmeng.pinduoduo/mdkd/package?tab=ID_CODE&entry_source=11&refer_page_name=login&refer_page_sn=10169",
                    "pinduoduo://com.xunmeng.pinduoduo/mdkd/package?tab=ID_CODE&entry_source=11",
                    "pinduoduo://com.xunmeng.pinduoduo/mdkd/package"
            },
            "https://m.pinduoduo.net/mdkd/package?entry_source=18&extra_params=from_wx_jump%3D1&p_channel=0"
    ),
    PINDUODUO_PENDING(
            "pinduoduo_pending",
            "拼多多待取快递",
            "拼多多待取快递",
            "包裹待取列表",
            "拼",
            "com.xunmeng.pinduoduo",
            new String[] {
                    "pinduoduo://com.xunmeng.pinduoduo/mdkd/package"
            },
            "https://m.pinduoduo.net/mdkd/package?entry_source=18&extra_params=from_wx_jump%3D1&p_channel=0"
    ),
    JD(
            "jd",
            "京东待取快递",
            "京东待取快递",
            "京东订单列表",
            "京",
            "com.jingdong.app.mall",
            new String[] {
                    "openjd://virtual?params=%7B%22category%22%3A%22jump%22%2C%22des%22%3A%22orderlist%22%7D"
            },
            "https://order.jd.com/center/list.action"
    ),
    XHS(
            "xhs",
            "小红书待取快递",
            "小红书待取快递",
            "小红书订单列表",
            "书",
            "com.xingin.xhs",
            new String[] {
                    "xhsdiscover://rn/lancer-order/order/list"
            },
            "https://www.xiaohongshu.com/"
    ),
    MEITUAN(
            "meituan",
            "美团订单",
            "美团待取快递",
            "美团订单列表",
            "美",
            "com.sankuai.meituan",
            new String[] {
                    "imeituan://www.meituan.com/orderTab"
            },
            "https://i.meituan.com/mttouch/page/order"
    ),
    DOUYIN(
            "douyin",
            "抖音商城",
            "抖音待取快递",
            "抖音商城列表",
            "抖",
            "com.ss.android.ugc.aweme",
            new String[] {
                    "snssdk1128://mall/xtab"
            },
            "https://www.douyin.com/"
    );

    final String key;
    final String title;
    /** 首页入口主标题。 */
    final String label;
    /** 首页入口副标题。 */
    final String subtitle;
    final String mark;
    final String packageName;
    final String[] appUris;
    final String webUri;
    final boolean openAppWhenDeepLinkUnavailable;

    Destination(
            String key,
            String title,
            String label,
            String subtitle,
            String mark,
            String packageName,
            String[] appUris,
            String webUri
    ) {
        this(key, title, label, subtitle, mark, packageName, appUris, webUri, false);
    }

    Destination(
            String key,
            String title,
            String label,
            String subtitle,
            String mark,
            String packageName,
            String[] appUris,
            String webUri,
            boolean openAppWhenDeepLinkUnavailable
    ) {
        this.key = key;
        this.title = title;
        this.label = label;
        this.subtitle = subtitle;
        this.mark = mark;
        this.packageName = packageName;
        this.appUris = appUris;
        this.webUri = webUri;
        this.openAppWhenDeepLinkUnavailable = openAppWhenDeepLinkUnavailable;
    }

    static Destination fromKey(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        for (Destination destination : values()) {
            if (destination.key.equals(normalized)) {
                return destination;
            }
        }
        return null;
    }
}
