package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 使用状态枚举
 */
@Getter
@AllArgsConstructor
public enum UseStatusEnum implements ArrayValuable<String> {

    NORMAL("1", "正常"),
    CANCELLED("2", "销户");

    public static final String[] ARRAYS = Arrays.stream(values()).map(UseStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
