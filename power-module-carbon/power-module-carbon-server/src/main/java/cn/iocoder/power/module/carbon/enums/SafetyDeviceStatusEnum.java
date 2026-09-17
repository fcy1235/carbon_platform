package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 安全装置配备情况枚举
 */
@Getter
@AllArgsConstructor
public enum SafetyDeviceStatusEnum implements ArrayValuable<String> {

    ALARM_SHUTOFF_VALVE("1", "报警器-切断阀"),
    SELF_CLOSING_VALVE("2", "自闭阀"),
    BOTH("3", "以上两者都有");

    public static final String[] ARRAYS = Arrays.stream(values()).map(SafetyDeviceStatusEnum::getType).toArray(String[]::new);

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
