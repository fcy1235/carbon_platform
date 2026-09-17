package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 改造批次枚举
 */
@Getter
@AllArgsConstructor
public enum ReformBatchEnum implements ArrayValuable<String> {

    NATIONAL_TASK("1", "国家部委下达任务"),
    PROVINCIAL_TASK("2", "省下达任务"),
    CITY_SELF("3", "市级自定完成"),
    CITY_SELF_FARMER("4", "市级自定-农户自改");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ReformBatchEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
