package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 燃气表类型枚举
 */
@Getter
@AllArgsConstructor
public enum GasMeterTypeEnum implements ArrayValuable<String> {

    IOT_WIRELESS("1", "物联网燃气表（无线远传）"),
    IC_CARD("2", "IC卡燃气表"),
    TRADITIONAL_DIAPHRAGM("3", "传统机器膜式燃气表"),
    OTHER("4", "其他");

    public static final String[] ARRAYS = Arrays.stream(values()).map(GasMeterTypeEnum::getType).toArray(String[]::new);

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
