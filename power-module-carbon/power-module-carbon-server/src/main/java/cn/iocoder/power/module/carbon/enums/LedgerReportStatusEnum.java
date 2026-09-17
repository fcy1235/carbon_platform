package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 台账上报流程状态枚举
 */
@Getter
@AllArgsConstructor
public enum LedgerReportStatusEnum implements ArrayValuable<String> {

    DRAFT("1", "待提交"),
    AUDITING("2", "审核中"),
    APPROVED("3", "已通过"),
    REJECTED("4", "已驳回");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LedgerReportStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static boolean isDraft(String status) {
        return ObjUtil.equal(DRAFT.type, status);
    }

    public static boolean isAuditing(String status) {
        return ObjUtil.equal(AUDITING.type, status);
    }

    public static boolean isApproved(String status) {
        return ObjUtil.equal(APPROVED.type, status);
    }

    public static boolean isRejected(String status) {
        return ObjUtil.equal(REJECTED.type, status);
    }
}
