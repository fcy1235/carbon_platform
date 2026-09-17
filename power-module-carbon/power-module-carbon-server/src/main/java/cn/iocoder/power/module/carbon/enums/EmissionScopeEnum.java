package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 排放范围枚举
 */
@Getter
@AllArgsConstructor
public enum EmissionScopeEnum implements ArrayValuable<String> {

    SCOPE_1("1", "直接排放"),
    SCOPE_2("2", "间接排放"),
    SCOPE_3("3", "其他间接排放");

    public static final String[] ARRAYS = Arrays.stream(values()).map(EmissionScopeEnum::getType).toArray(String[]::new);

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
