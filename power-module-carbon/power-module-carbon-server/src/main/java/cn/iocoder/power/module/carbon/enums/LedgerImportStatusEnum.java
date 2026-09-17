package cn.iocoder.power.module.carbon.enums;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 台账导入任务状态枚举
 */
@Getter
@AllArgsConstructor
public enum LedgerImportStatusEnum implements ArrayValuable<String> {

    PENDING_CHECK("1", "待导入"),

    IMPORTING("2", "导入中"),
    IMPORT_SUCCESS("3", "导入成功"),
    PARTIAL_FAIL("4", "部分失败"),
    IMPORT_FAIL("5", "导入失败");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LedgerImportStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static boolean isPendingCheck(String status) {
        return ObjUtil.equal(PENDING_CHECK.type, status);
    }


    public static boolean isImporting(String status) {
        return ObjUtil.equal(IMPORTING.type, status);
    }
}
