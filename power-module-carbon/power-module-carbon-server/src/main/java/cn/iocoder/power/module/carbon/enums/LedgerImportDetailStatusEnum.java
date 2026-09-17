package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 台账导入明细状态枚举
 */
@Getter
@AllArgsConstructor
public enum LedgerImportDetailStatusEnum implements ArrayValuable<String> {

    SUCCESS("1", "成功"),
    FAIL("2", "失败");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LedgerImportDetailStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
