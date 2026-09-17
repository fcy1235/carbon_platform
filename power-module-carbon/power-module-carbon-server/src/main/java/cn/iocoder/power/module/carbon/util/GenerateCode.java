package cn.iocoder.power.module.carbon.util;

import cn.hutool.core.date.DatePattern;
import cn.iocoder.power.framework.common.util.date.LocalDateTimeUtils;

public class GenerateCode {
    public static String generateCode(String prefix, String lastCode) {
        String todayStr = LocalDateTimeUtils.getTodayWithFormat(DatePattern.PURE_DATE_PATTERN);
        prefix += todayStr;
        // 检查是否为当天记录（格式：ES + 8位日期 + 3位数字 = 14位总长度）
        if (lastCode != null && lastCode.startsWith(prefix)) {
            try {
                // 直接从末尾取3位数字进行递增
                int currentNum = Integer.parseInt(lastCode.substring(11));
                return prefix + String.format("%03d", currentNum + 1);
            } catch (NumberFormatException e) {
                // 数字解析失败，从001开始
                return prefix + "001";
            }
        }

        // 不是当天记录或无历史记录，从001开始
        return prefix + "001";
    }

    /**
     * 生成「前缀-6位序号」编码，如 GM-000001。
     * 序号按前缀独立递增，不含日期；每次基于库内该前缀最大编码 +1，保证唯一。
     *
     * @param prefix   编码前缀（如 EM/GM/ASHP）
     * @param lastCode 库内该前缀最新的编码，无历史记录时传 null
     */
    public static String generateSeqCode(String prefix, String lastCode) {
        String prefixWithDash = prefix + "-";
        int next = 1;
        if (lastCode != null && lastCode.startsWith(prefixWithDash)) {
            try {
                next = Integer.parseInt(lastCode.substring(prefixWithDash.length())) + 1;
            } catch (NumberFormatException e) {
                // 序号解析失败，从 1 开始
                next = 1;
            }
        }
        return prefixWithDash + String.format("%06d", next);
    }

}
