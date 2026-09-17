package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 补贴状态枚举
 */
@Getter
@AllArgsConstructor
public enum SubsidyStatusEnum implements ArrayValuable<String> {

    PENDING("1", "待申请"),
    AUDITING("2", "审核中"),
    GRANTED("3", "已发放"),
    REJECTED("4", "已驳回"),
    NOT_GRANTED("5", "未发放");

    public static final String[] ARRAYS = Arrays.stream(values()).map(SubsidyStatusEnum::getType).toArray(String[]::new);

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

    public static boolean isGranted(String status) {
        return ObjUtil.equal(GRANTED.type, status);
    }

}
