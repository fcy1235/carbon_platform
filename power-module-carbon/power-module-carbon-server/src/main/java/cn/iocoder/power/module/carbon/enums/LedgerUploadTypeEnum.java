package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 台账上传类型枚举
 */
@Getter
@AllArgsConstructor
public enum LedgerUploadTypeEnum implements ArrayValuable<String> {

    NEW_USER("1", "新增户"),
    CHANGE_USER("2", "变更户"),
    CANCEL_USER("3", "撤销户"),
    REFORM_CHANGE("4", "改造类型变更");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LedgerUploadTypeEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
