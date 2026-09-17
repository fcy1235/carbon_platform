package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 设备类型枚举
 */
@Getter
@AllArgsConstructor
public enum DeviceTypeEnum implements ArrayValuable<String> {

    HEATING_EQUIPMENT("1", "采暖设备"),
    ENERGY_SAVING_MATERIAL("2", "节能建材"),
    POWER_EQUIPMENT("3", "电力设备"),
    GAS_EQUIPMENT("4", "燃气设备");

    public static final String[] ARRAYS = Arrays.stream(values()).map(DeviceTypeEnum::getType).toArray(String[]::new);

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
