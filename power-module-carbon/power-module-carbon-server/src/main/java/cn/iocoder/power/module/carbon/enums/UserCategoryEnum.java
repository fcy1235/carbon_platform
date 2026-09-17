package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户分类枚举
 */
@Getter
@AllArgsConstructor
public enum UserCategoryEnum implements ArrayValuable<String> {

    NORMAL("1", "正常用户"),
    IDLE("2", "闲置用户"),
    OTHER("3", "其他用户");

    public static final String[] ARRAYS = Arrays.stream(values()).map(UserCategoryEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
