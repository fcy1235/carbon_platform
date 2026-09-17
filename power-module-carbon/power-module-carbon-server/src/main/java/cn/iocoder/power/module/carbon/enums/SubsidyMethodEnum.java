package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 发放补贴方式枚举
 */
@Getter
@AllArgsConstructor
public enum SubsidyMethodEnum implements ArrayValuable<String> {

    BANK_CARD("1", "银行卡"),
    ONE_CARD("2", "一卡通"),
    CASH("3", "现金"),
    GAS_METER("4", "气表"),
    ELECTRIC_METER("5", "电表");

    public static final String[] ARRAYS = Arrays.stream(values()).map(SubsidyMethodEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
