package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 燃气安全员人员状态枚举
 */
@Getter
@AllArgsConstructor
public enum GasSafetyOfficerStatusEnum implements ArrayValuable<String> {

    ON_DUTY("1", "在岗"),
    OFF_DUTY("2", "离岗");

    public static final String[] ARRAYS = Arrays.stream(values()).map(GasSafetyOfficerStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
