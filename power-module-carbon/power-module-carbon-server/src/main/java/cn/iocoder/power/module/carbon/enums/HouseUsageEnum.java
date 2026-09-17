package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 房屋用途枚举
 */
@Getter
@AllArgsConstructor
public enum HouseUsageEnum implements ArrayValuable<String> {

    RESIDENTIAL("1", "用于居住"),
    COMMERCIAL("2", "用于经营"),
    MIXED_USE("3", "商住两用");

    public static final String[] ARRAYS = Arrays.stream(values()).map(HouseUsageEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
