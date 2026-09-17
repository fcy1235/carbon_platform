package cn.iocoder.power.module.carbon.dal.redis;

/**
 * Carbon 模块 Redis Key 常量类
 */
public interface RedisKeyConstants {

    /**
     * 基线数据的缓存
     * <p>
     * KEY 格式：carbon:baseline:{id}
     * VALUE 数据类型：String 基线信息
     */
    String CARBON_BASELINE = "carbon:baseline";

    /**
     * 联系人的缓存
     * <p>
     * KEY 格式：carbon:contact:{id}
     * VALUE 数据类型：String 联系人信息
     */
    String CARBON_CONTACT = "carbon:contact";

    /**
     * 用户基本信息的缓存
     * <p>
     * KEY 格式：carbon:user_info:{id}
     * VALUE 数据类型：String 用户基本信息
     */
    String CARBON_USER_INFO = "carbon:user_info";

    /**
     * 参数库的缓存
     * <p>
     * KEY 格式：carbon:param_lib:{id}
     * VALUE 数据类型：String 参数库信息
     */
    String CARBON_PARAM_LIB = "carbon:param_lib";

    /**
     * 因子库的缓存
     * <p>
     * KEY 格式：carbon:factor_lib:{id}
     * VALUE 数据类型：String 因子库信息
     */
    String CARBON_FACTOR_LIB = "carbon:factor_lib";

    /**
     * 排放源的缓存
     * <p>
     * KEY 格式：carbon:emission_source:{id}
     * VALUE 数据类型：String 排放源信息
     */
    String CARBON_EMISSION_SOURCE = "carbon:emission_source";

}
