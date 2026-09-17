package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 网点经营状态枚举
 */
@Getter
@AllArgsConstructor
public enum StationBusinessStatusEnum implements ArrayValuable<String> {

    NOT_OPERATING("1", "未经营"),
    OPERATING("2", "经营中"),
    CLOSED("3", "已关闭");

    public static final String[] ARRAYS = Arrays.stream(values()).map(StationBusinessStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
