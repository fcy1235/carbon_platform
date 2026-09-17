package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 所属气候子区枚举
 */
@Getter
@AllArgsConstructor
public enum ClimateZoneEnum implements ArrayValuable<String> {

    COLD_A("1", "寒冷A区"),
    COLD_B("2", "寒冷B区"),
    COLD_C("3", "寒冷C区");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ClimateZoneEnum::getType).toArray(String[]::new);

    /**
     * 类型值
     */
    private final String type;
    /**
     * 类型名称
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
