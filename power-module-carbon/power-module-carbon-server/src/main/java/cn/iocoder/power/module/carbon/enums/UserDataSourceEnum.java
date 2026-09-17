package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户数据来源枚举
 */
@Getter
@AllArgsConstructor
public enum UserDataSourceEnum implements ArrayValuable<String> {

    DEVICE_DATA("1", "设备数据"),
    INTERFACE_DATA("2", "接口数据");

    public static final String[] ARRAYS = Arrays.stream(values()).map(UserDataSourceEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
