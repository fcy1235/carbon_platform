package cn.iocoder.power.module.carbon.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.extra.pinyin.engine.jpinyin.JPinyinEngine;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.enums.UserDataSourceEnum;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 用户编码生成器
 *
 * 接口数据用户：固定前缀 API-USER- + 流水号，如 API-USER-001
 * 其他用户：(省缩写)-(市缩写)-(区县缩写)- + 流水号，如 HB-SJZ-YH-000001
 * 区域缩写 = 名称去掉最后一个区划字后的核心名拼音首字母（如 河北省 -> HB），取自行政区数据的 pinyin_prefix
 *
 * 流水号通过数据库序列表 carbon_user_code_sequence 原子取号：
 * 单条 INSERT ... ON CONFLICT DO UPDATE ... RETURNING 保证并发安全、单调递增（删除不复用），
 * 容量为 BIGINT 不限量；编码按最短位数补零，超出位数自动扩展
 */
@Component
public class UserCodeGenerator {

    /** 接口数据用户编码前缀 */
    private static final String API_USER_CODE_PREFIX = "API-USER-";
    /** 接口数据用户流水号最小位数 */
    private static final int API_USER_SEQ_LENGTH = 3;
    /** 区域用户流水号最小位数 */
    private static final int AREA_USER_SEQ_LENGTH = 6;
    /** 缺少行政区划信息时的兜底前缀 */
    private static final String FALLBACK_CODE_PREFIX = "USER-";

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private AreaApi areaApi;

    /**
     * 生成用户编码
     *
     * @param dataSource   数据来源（1 设备数据 2 接口数据）
     * @param provinceCode 行政区划-省编码
     * @param cityCode     行政区划-市编码
     * @param districtCode 行政区划-区县编码
     * @return 用户编码
     */
    public String generate(String dataSource, Long provinceCode, Long cityCode, Long districtCode) {
        // 接口数据用户：API-USER- + 3 位流水号
        if (UserDataSourceEnum.INTERFACE_DATA.getType().equals(dataSource)) {
            return generateByPrefix(API_USER_CODE_PREFIX, API_USER_SEQ_LENGTH);
        }
        // 其他用户：省-市-区县 拼音缩写 + 6 位流水号
        String prefix = buildAreaPrefix(provinceCode, cityCode, districtCode);
        if (StrUtil.isBlank(prefix)) {
            prefix = FALLBACK_CODE_PREFIX;
        }
        return generateByPrefix(prefix, AREA_USER_SEQ_LENGTH);
    }

    /**
     * 拼接省市区县拼音缩写前缀，如 HB-SJZ-YH-
     * 存在空缺或无法解析的层级直接跳过
     */
    private String buildAreaPrefix(Long provinceCode, Long cityCode, Long districtCode) {
        Set<Long> areaIds = new HashSet<>();
        areaIds.add(provinceCode);
        areaIds.add(cityCode);
        areaIds.add(districtCode);
        areaIds.remove(null);
        if (CollUtil.isEmpty(areaIds)) {
            return null;
        }
        CommonResult<List<AreaRespDTO>> areaResult = areaApi.getAreaList(areaIds);
        List<AreaRespDTO> areaList = areaResult.getData();
        if (CollUtil.isEmpty(areaList)) {
            return null;
        }
        Map<Long, AreaRespDTO> areaMap = CollectionUtils.convertMap(areaList, AreaRespDTO::getId, area -> area);

        StringBuilder prefix = new StringBuilder();
        appendAbbr(prefix, toAbbr(areaMap.get(provinceCode)));
        appendAbbr(prefix, toAbbr(areaMap.get(cityCode)));
        appendAbbr(prefix, toAbbr(areaMap.get(districtCode)));
        return prefix.toString();
    }

    private void appendAbbr(StringBuilder prefix, String abbr) {
        if (StrUtil.isBlank(abbr)) {
            return;
        }
        if (prefix.length() > 0) {
            prefix.append("-");
        }
        prefix.append(abbr);
    }

    /**
     * 行政区转拼音缩写：名称去掉最后一个字（区划后缀字），再取核心名的拼音首字母
     * 如 河北省 -> 河北 -> hbs 取前 2 位 -> HB、石家庄市 -> 石家庄 -> sjzs 取前 3 位 -> SJZ、裕华区 -> 裕华 -> yhq 取前 2 位 -> YH
     * 拼音首字母取自行政区数据的 pinyin_prefix（每个汉字对应 1 位）
     */
    private String toAbbr(AreaRespDTO area) {
        if (area == null || StrUtil.isBlank(area.getName()) || StrUtil.isBlank(area.getPinyinPrefix())) {
            return null;
        }
        String name = area.getName().trim();
        // 去掉最后一个字（省/市/区/县等区划后缀字），单字名称不去
        String coreName = name.length() > 1 ? name.substring(0, name.length() - 1) : name;

        String prefix = PinyinUtil.getFirstLetter(coreName,"");
        // pinyin_prefix 长度不足核心名时（数据不完整），按实际长度返回
        int endIndex = Math.min(prefix.length(), coreName.length());
        return endIndex == 0 ? null : prefix.substring(0, endIndex).toUpperCase(Locale.ROOT);
    }

    /**
     * 从序列表原子取号后格式化生成编码
     * 单条 INSERT ... ON CONFLICT DO UPDATE ... RETURNING 取号，并发下每个前缀的流水号单调递增且不重复
     *
     * @param prefix    编码前缀（同时作为序列表主键）
     * @param seqLength 流水号最短位数，不足前面补 0，超出自动扩展
     */
    private String generateByPrefix(String prefix, int seqLength) {
        Long nextSeq = jdbcTemplate.queryForObject(
                "INSERT INTO carbon_user_code_sequence (prefix, next_value) VALUES (?, 1)" +
                " ON CONFLICT (prefix) DO UPDATE SET next_value = carbon_user_code_sequence.next_value + 1" +
                " RETURNING next_value",
                Long.class, prefix);
        long seq = nextSeq != null ? nextSeq : 1L;
        return prefix + String.format("%0" + seqLength + "d", seq);
    }

}
