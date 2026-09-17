package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 企业服务状态枚举
 */
@Getter
@AllArgsConstructor
public enum EnterpriseServiceStatusEnum implements ArrayValuable<String> {

    SERVING("1", "服务中"),
    STOPPED("2", "已停止");

    public static final String[] ARRAYS = Arrays.stream(values()).map(EnterpriseServiceStatusEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
