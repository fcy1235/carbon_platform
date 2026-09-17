package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 设备状态枚举
 */
@Getter
@AllArgsConstructor
public enum DeviceStatusEnum implements ArrayValuable<String> {

    RUNNING("1", "运行中"),
    STOPPED("2", "已停机"),
    MAINTENANCE("3", "维修中");

    public static final String[] ARRAYS = Arrays.stream(values()).map(DeviceStatusEnum::getType).toArray(String[]::new);

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
