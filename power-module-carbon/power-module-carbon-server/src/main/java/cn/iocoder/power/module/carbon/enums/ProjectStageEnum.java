package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 项目阶段枚举
 */
@Getter
@AllArgsConstructor
public enum ProjectStageEnum implements ArrayValuable<String> {

    DECLARATION("1", "申报"),
    APPROVAL("2", "审批"),
    IMPLEMENTATION("3", "实施"),
    ACCEPTANCE("4", "验收"),
    COMPLETION("5", "结束");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ProjectStageEnum::getType).toArray(String[]::new);

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

    public static String getNameByType(String type) {
        for (ProjectStageEnum value : values()) {
            if (ObjUtil.equal(value.type, type)) {
                return value.name;
            }
        }
        return "";
    }

}
