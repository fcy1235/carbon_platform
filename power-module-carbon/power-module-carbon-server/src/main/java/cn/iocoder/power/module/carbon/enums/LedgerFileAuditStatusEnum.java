package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 台账审定流程状态枚举
 */
@Getter
@AllArgsConstructor
public enum LedgerFileAuditStatusEnum implements ArrayValuable<String> {
    WAIT_AUDIT("0", "待审定"),
    DRAFT("1", "待上报"),
    AUDITING("2", "审核中"),
    APPROVED("3", "已通过"),
    REJECTED("4", "已驳回");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LedgerFileAuditStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static boolean isWait(String status) {
        return ObjUtil.equal(WAIT_AUDIT.type, status);
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
