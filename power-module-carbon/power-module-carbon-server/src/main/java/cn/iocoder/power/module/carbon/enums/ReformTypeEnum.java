package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 改造类别枚举
 */
@Getter
@AllArgsConstructor
public enum ReformTypeEnum implements ArrayValuable<String> {

    ELECTRIC_COAL("1", "煤改电"),
    GAS_COAL("2", "煤改气");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ReformTypeEnum::getType).toArray(String[]::new);

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
