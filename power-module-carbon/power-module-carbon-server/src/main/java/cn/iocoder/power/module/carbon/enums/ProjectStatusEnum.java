package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 项目状态枚举
 */
@Getter
@AllArgsConstructor
public enum ProjectStatusEnum implements ArrayValuable<String> {

    NOT_STARTED("1", "未启动"),
    IN_PROGRESS("2", "进行中"),
    COMPLETED("3", "已完成"),
    TERMINATED("4", "已终止");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ProjectStatusEnum::getType).toArray(String[]::new);

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

    public static boolean isNotStarted(String status) {
        return ObjUtil.equal(NOT_STARTED.type, status);
    }

    public static boolean isInProgress(String status) {
        return ObjUtil.equal(IN_PROGRESS.type, status);
    }

    public static boolean isCompleted(String status) {
        return ObjUtil.equal(COMPLETED.type, status);
    }

    public static String getNameByType(String type) {
        for (ProjectStatusEnum value : values()) {
            if (ObjUtil.equal(value.type, type)) {
                return value.name;
            }
        }
        return "";
    }

}
