package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 参数分类枚举
 */
@Getter
@AllArgsConstructor
public enum ParamCategoryEnum implements ArrayValuable<String> {

    EMISSION_FACTOR("1", "排放因子"),
    METEOROLOGICAL("2", "气象参数"),
    ENERGY_CONSUMPTION("3", "能耗参数");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ParamCategoryEnum::getType).toArray(String[]::new);

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
