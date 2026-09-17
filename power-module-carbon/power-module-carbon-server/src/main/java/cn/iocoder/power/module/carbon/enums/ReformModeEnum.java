package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 改造类型枚举
 */
@Getter
@AllArgsConstructor
public enum ReformModeEnum implements ArrayValuable<String> {

    GAS_COAL("1", "气代煤"),
    DIRECT_ELECTRIC_BOILER("2", "直热式电锅炉"),
    STORAGE_ELECTRIC_BOILER("3", "蓄热式电锅炉"),
    ELECTRIC_WALL_HUNG_BOILER("4", "电壁挂炉"),
    DIRECT_ELECTRIC_HEATER("5", "直热式电暖器"),
    STORAGE_ELECTRIC_HEATER("6", "蓄热式电暖器"),
    GRAPHENE("7", "石墨烯"),
    JUNENG("8", "聚能"),
    AIR_SOURCE_HEAT_FAN("9", "空气源（能）热风机"),
    AIR_SOURCE_HEAT_PUMP("10", "空气源热泵"),
    GROUND_SOURCE_HEAT_PUMP("11", "地源热泵"),
    CENTRAL_HEATING("12", "集中供热"),
    PHOTOVOLTAIC("13", "光伏"),
    SOLAR_THERMAL("14", "光热"),
    ALCOHOL_BASED_FUEL("15", "醇基燃料"),
    BIOMASS("16", "生物质");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ReformModeEnum::getType).toArray(String[]::new);

    private final String type;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
