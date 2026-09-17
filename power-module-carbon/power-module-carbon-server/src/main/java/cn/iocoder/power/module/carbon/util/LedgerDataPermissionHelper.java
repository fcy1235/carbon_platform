package cn.iocoder.power.module.carbon.util;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.security.core.LoginUser;
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 台账模块数据权限辅助类
 * <p>
 * 根据当前登录用户的所属区域（市/区县）控制数据范围：
 * <ul>
 *   <li>县级用户：仅可查看、操作本区县数据</li>
 *   <li>市级用户：可查看、操作本市辖区内区县数据</li>
 * </ul>
 * <p>
 * 区域信息优先从 {@link LoginUser#getContext()} 中获取，其次从 {@link LoginUser#getInfo()} 中获取。
 * 若当前框架未在登录用户中存储区域信息，则视为不限制（需由业务系统补齐用户区域上下文）。
 */
@Slf4j
public class LedgerDataPermissionHelper {

    public static final String CONTEXT_KEY_CITY_CODE = "cityCode";
    public static final String CONTEXT_KEY_CITY_NAME = "cityName";
    public static final String CONTEXT_KEY_DISTRICT_CODE = "districtCode";
    public static final String CONTEXT_KEY_DISTRICT_NAME = "districtName";

    /**
     * 数据权限范围
     */
    public enum DataScope {
        /**
         * 无限制
         */
        NONE,
        /**
         * 市级：按市编码过滤
         */
        CITY,
        /**
         * 县级：按区县编码过滤
         */
        DISTRICT
    }

    /**
     * 当前登录用户的区域上下文
     */
    public static class UserAreaContext {

        private final DataScope scope;
        private final String cityCode;
        private final String cityName;
        private final String districtCode;
        private final String districtName;

        public UserAreaContext(DataScope scope, String cityCode, String cityName,
                               String districtCode, String districtName) {
            this.scope = scope;
            this.cityCode = cityCode;
            this.cityName = cityName;
            this.districtCode = districtCode;
            this.districtName = districtName;
        }

        public DataScope getScope() {
            return scope;
        }

        public String getCityCode() {
            return cityCode;
        }

        public String getCityName() {
            return cityName;
        }

        public String getDistrictCode() {
            return districtCode;
        }

        public String getDistrictName() {
            return districtName;
        }
    }

    /**
     * 获取当前登录用户的数据权限范围
     */
    public static UserAreaContext getCurrentUserAreaContext() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            log.warn("当前登录用户为空，台账数据权限不生效");
            return new UserAreaContext(DataScope.NONE, null, null, null, null);
        }

        String cityCode = getStringValue(loginUser, CONTEXT_KEY_CITY_CODE);
        String cityName = getStringValue(loginUser, CONTEXT_KEY_CITY_NAME);
        String districtCode = getStringValue(loginUser, CONTEXT_KEY_DISTRICT_CODE);
        String districtName = getStringValue(loginUser, CONTEXT_KEY_DISTRICT_NAME);

        if (StrUtil.isNotBlank(districtCode)) {
            return new UserAreaContext(DataScope.DISTRICT, cityCode, cityName, districtCode, districtName);
        }
        if (StrUtil.isNotBlank(cityCode)) {
            return new UserAreaContext(DataScope.CITY, cityCode, cityName, null, null);
        }

        log.warn("当前登录用户未配置区域信息（cityCode/districtCode），台账数据权限不生效");
        return new UserAreaContext(DataScope.NONE, null, null, null, null);
    }

    /**
     * 从 LoginUser 中按 key 获取字符串值，优先 context，其次 info
     */
    private static String getStringValue(LoginUser loginUser, String key) {
        // 1. 优先从 context 中获取（支持 Object 类型）
        if (loginUser.getContext() != null) {
            Object value = loginUser.getContext().get(key);
            if (value != null) {
                return value.toString();
            }
        }
        // 2. 从 info 中获取（String 类型）
        if (loginUser.getInfo() != null) {
            return loginUser.getInfo().get(key);
        }
        return null;
    }
}
