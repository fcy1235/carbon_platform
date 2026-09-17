package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 能碳气体类别枚举
 */
@Getter
@AllArgsConstructor
public enum GasCategoryEnum implements ArrayValuable<String> {

    CO2("1", "二氧化碳（CO₂）"),
    CH4("2", "甲烷（CH₄）"),
    N2O("3", "氧化亚氮（N₂O）"),
    HFCs("4", "氢氟碳化合物（HFCs）"),
    PFCs("5", "全氟碳化合物（PFCs）"),
    SF6("6", "六氟化硫（SF₆）");

    public static final String[] ARRAYS = Arrays.stream(values()).map(GasCategoryEnum::getType).toArray(String[]::new);

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
