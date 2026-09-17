package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportErrorVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportResultVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDeviceMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceEnterpriseMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.enums.DeviceStatusEnum;
import cn.iocoder.power.module.carbon.enums.DeviceTypeEnum;
import cn.iocoder.power.module.carbon.enums.GasMeterTypeEnum;
import cn.iocoder.power.module.carbon.enums.ReformTypeEnum;
import cn.iocoder.power.module.carbon.enums.SafetyDeviceStatusEnum;
import cn.iocoder.power.module.carbon.service.CarbonDeviceImportService;
import cn.iocoder.power.module.carbon.util.GenerateCode;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 设备信息导入 Service 实现
 *
 * 导入规则：
 * 1. 模板有值的字段优先取模板值，并按表头列名映射到对应实体（设备/用户/供气企业）；
 * 2. 模板为空的字段按关键字段（用户编码/身份证号/户主姓名）查询已有「用户基本信息」补全，查不到则新建用户；
 * 3. 供气企业按「企业信用代码/企业名称」匹配已有企业；
 * 4. 单行失败不阻断整批，逐行记录错误，最终返回成功/失败行数与错误明细。
 */
@Service
@Validated
@Slf4j
public class CarbonDeviceImportServiceImpl implements CarbonDeviceImportService {

    @Resource
    private CarbonDeviceMapper deviceMapper;

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Resource
    private CarbonMaintenanceEnterpriseMapper enterpriseMapper;

    @Resource
    private AreaApi areaApi;

    /**
     * 默认省份：河北省，编码 13（与台账导入一致；模板不含省份列）
     */
    private static final Long DEFAULT_PROVINCE_CODE = 13L;

    private static final Set<String> VALID_GAS_METER_TYPES = Set.of("1", "2", "3", "4");
    private static final Set<String> VALID_SAFETY_STATUS = Set.of("1", "2", "3");

    /** 导入的设备均为燃气表，编码前缀固定 GM */
    private static final String GAS_METER_CODE_PREFIX = "GM";

    private static final AtomicLong IMPORT_SEQ = new AtomicLong(System.currentTimeMillis());

    @Override
    public CarbonDeviceImportResultVO importDevice(MultipartFile file) throws IOException {
        List<CarbonDeviceImportVO> list = ExcelUtils.read(file, CarbonDeviceImportVO.class);

        CarbonDeviceImportResultVO result = new CarbonDeviceImportResultVO();
        List<CarbonDeviceImportErrorVO> errors = new ArrayList<>();
        int successCount = 0;
        // 表头占第 1 行，数据从第 2 行开始
        int rowNum = 1;
        for (CarbonDeviceImportVO vo : list) {
            rowNum++;
            if (isBlankRow(vo)) {
                continue;
            }
            try {
                importRow(vo);
                successCount++;
            } catch (Exception e) {
                String msg = e.getMessage();
                errors.add(new CarbonDeviceImportErrorVO(rowNum, StrUtil.isBlank(msg) ? "导入失败" : msg));
            }
        }

        int failCount = errors.size();
        result.setTotalCount(successCount + failCount);
        result.setSuccessCount(successCount);
        result.setFailCount(failCount);
        result.setErrors(errors);
        // 暴露真实处理行数，便于排查 "导入成功但列表无变化" 的问题
        log.info("[设备信息导入] Excel 读取行数={}，成功={}，失败={}",
                successCount + failCount, successCount, failCount);
        return result;
    }

    /**
     * 判断是否为整行为空的行，空行直接跳过
     */
    private boolean isBlankRow(CarbonDeviceImportVO vo) {
        String[] fields = {
                vo.getUsername(), vo.getIdCard(), vo.getUserCode(),
                vo.getCityName(), vo.getDistrictName(), vo.getTownName(), vo.getVillageName(),
                vo.getAddress(), vo.getReformType(), vo.getEnterpriseName(), vo.getEnterpriseCreditCode(),
                vo.getWallMountedStoveInstallYear(), vo.getGasId(), vo.getDeviceBrandModel(),
                vo.getGasMeterType(), vo.getSafetyDeviceStatus()
        };
        for (String field : fields) {
            if (StrUtil.isNotBlank(field)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 导入单行：解析用户 → 解析供气企业 → 创建设备
     *
     * fallback 规则（模板某字段为空时回查关联表）：
     * - 供气企业：模板「企业名称」为空时，用「企业信用代码」去 carbon_maintenance_enterprise 匹配（见 resolveEnterprise）
     * - 燃气表具号：模板为空时，回查已维护的 carbon_user_info.gas_id（用户存在时才可见，见 resolveUser）
     * - 燃气表品牌及型号：模板为空时暂不填，待业务确认回查来源（TODO）
     * - 壁挂炉安装年份有值时，顺带作为 生产日期/安装日期/投运日期 的年份默认值，避免日期字段为空
     */
    private void importRow(CarbonDeviceImportVO vo) {
        Long userId = resolveUser(vo);
        Long enterpriseId = resolveEnterprise(vo);

        CarbonDeviceDO device = new CarbonDeviceDO();
        device.setCarbonUserInfoId(userId);
        device.setGasEnterpriseId(enterpriseId);
        // 模板有值的设备字段
        device.setWallMountedStoveInstallYear(trim(vo.getWallMountedStoveInstallYear()));
        device.setGasMeterType(normalizeGasMeterType(vo.getGasMeterType()));
        device.setSafetyDeviceStatus(normalizeSafetyDeviceStatus(vo.getSafetyDeviceStatus()));
        // 燃气表品牌及型号：模板为空时保持空（非必填），回查来源待业务确认 —— 用户原话"如果为空的话就去…"未说完
        // 已核实：carbon_user_info / carbon_maintenance_enterprise 均无品牌型号字段，暂无可回查来源
        device.setDeviceBrandModel(trim(vo.getDeviceBrandModel()));
        // 模板未提供的设备字段，给合理默认值（编码自动生成，类型按「燃气设备」，状态默认「运行中」）
        device.setDeviceCode(generateDeviceCode());
        device.setDeviceName("燃气表");
        device.setDeviceType(DeviceTypeEnum.ENERGY_SAVING_MATERIAL.getType());
        device.setStatus(DeviceStatusEnum.RUNNING.getType());
        // 补全表单必填字段：由「壁挂炉安装年份」推导日期，避免"查看"页面日期为空
        String yearStr = trim(vo.getWallMountedStoveInstallYear());
        if (StrUtil.isNotBlank(yearStr)) {
            try {
                LocalDate yearFirstDay = LocalDate.of(Integer.parseInt(yearStr), 1, 1);
                device.setProductionDate(yearFirstDay);
                device.setInstallDate(yearFirstDay);
                device.setOperationDate(yearFirstDay);
            } catch (NumberFormatException ignore) {
                // 年份非法时不做强制推导，保持为空
            }
        }
        // 使用年限给通用默认值（燃气表类设备通常 8 年），避免为空
//        device.setServiceLife(8);
        deviceMapper.insert(device);
    }

    /**
     * 查找或创建用户，返回用户 ID。
     * 依次按 用户编码 → 身份证号 → 户主姓名 匹配已有用户；均未匹配到则新建用户。
     */
    private Long resolveUser(CarbonDeviceImportVO vo) {
        String username = trim(vo.getUsername());
        String idCard = trim(vo.getIdCard());
        String userCode = trim(vo.getUserCode());

        CarbonUserInfoDO user = null;
        if (StrUtil.isNotBlank(userCode)) {
            user = userInfoMapper.selectByUserCode(userCode);
        }
        if (user == null && StrUtil.isNotBlank(idCard)) {
            user = userInfoMapper.selectByIdCard(idCard);
        }
        if (user == null && StrUtil.isNotBlank(username)) {
            user = userInfoMapper.selectFirstOne(CarbonUserInfoDO::getUsername, username);
        }
        if (user != null) {
            return user.getId();
        }

        // 未匹配到，则新建用户
        if (StrUtil.isBlank(username)) {
            throw new IllegalArgumentException("户主姓名为空，且未匹配到已有用户");
        }
        CarbonUserInfoDO newUser = new CarbonUserInfoDO();
        newUser.setUsername(username);
        newUser.setIdCard(idCard);
        newUser.setUserCode(StrUtil.isNotBlank(userCode) ? userCode : generateUserCode());
        newUser.setAddress(trim(vo.getAddress()));
        newUser.setReformType(resolveReformType(vo.getReformType()));
        newUser.setGasId(trim(vo.getGasId()));
        // heating_area 数据库非空：模板未提供采暖面积，给默认值 0
        newUser.setHeatingArea(BigDecimal.ZERO);
        // 行政区划：province_code 数据库非空，必须回填。模板不含省份，默认河北省（13），
        // 再按「市→区县→乡镇→村」中文名称逐级反查编码（与台账导入逻辑一致）
        resolveAndSetAreaCodes(newUser, vo);
        userInfoMapper.insert(newUser);
        return newUser.getId();
    }

    /**
     * 根据模板的行政区中文名称反查编码并设置到用户信息。
     * 省份固定河北省（13），以省为父节点逐级查找 市→区县→乡镇→村；某级名称为空或未匹配到则该级为 null。
     */
    private void resolveAndSetAreaCodes(CarbonUserInfoDO userInfo, CarbonDeviceImportVO vo) {
        Long provinceCode = DEFAULT_PROVINCE_CODE;
        Long cityCode = resolveAreaCode(provinceCode, trim(vo.getCityName()));
        Long districtCode = resolveAreaCode(cityCode, trim(vo.getDistrictName()));
        Long townCode = resolveAreaCode(districtCode, trim(vo.getTownName()));
        Long villageCode = resolveAreaCode(townCode, trim(vo.getVillageName()));

        userInfo.setProvinceCode(provinceCode);
        userInfo.setCityCode(cityCode);
        userInfo.setDistrictCode(districtCode);
        userInfo.setTownCode(townCode);
        userInfo.setVillageCode(villageCode);
    }

    /**
     * 根据父级编码和行政区名称解析编码
     */
    private Long resolveAreaCode(Long parentId, String name) {
        if (parentId == null || StrUtil.isBlank(name)) {
            return null;
        }
        AreaRespDTO area = areaApi.getAreaByName(parentId, name).getData();
        return area != null ? area.getId() : null;
    }

    /**
     * 查找供气企业，返回企业 ID。
     * 如果模板填写了企业名称或企业信用代码，但未能匹配到维保企业，则抛出异常提示先创建供气企业信息。
     */
    private Long resolveEnterprise(CarbonDeviceImportVO vo) {
        String name = trim(vo.getEnterpriseName());
        String creditCode = trim(vo.getEnterpriseCreditCode());

        // 模板未填写任何企业信息，直接返回 null
        if (StrUtil.isBlank(name) && StrUtil.isBlank(creditCode)) {
            return null;
        }

        CarbonMaintenanceEnterpriseDO enterprise = null;
        if (StrUtil.isNotBlank(creditCode)) {
            enterprise = enterpriseMapper.selectByUnifiedSocialCreditCode(creditCode);
        }
        if (enterprise == null && StrUtil.isNotBlank(name)) {
            enterprise = enterpriseMapper.selectByEnterpriseName(name);
        }

        if (enterprise == null) {
            String identifier = StrUtil.isNotBlank(creditCode) ? "企业信用代码「" + creditCode + "」" : "企业名称「" + name + "」";
            throw new IllegalArgumentException("供气企业不存在：" + identifier + "，请先在维保企业中创建该供气企业信息");
        }
        return enterprise.getId();
    }

    /**
     * 燃气表类型：模板为数字编码 1-4，直接校验并存储编码
     */
    private String normalizeGasMeterType(String raw) {
        String value = trim(raw);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String code = extractCode(value);
        if (code == null) {
            code = matchEnumName(GasMeterTypeEnum.values(), value);
        }
        if (code == null || !VALID_GAS_METER_TYPES.contains(code)) {
            throw new IllegalArgumentException("燃气表类型非法：" + raw + "，可选 1/2/3/4");
        }
        return code;
    }

    /**
     * 安全装置配备情况：模板为数字编码 1-3，直接校验并存储编码
     */
    private String normalizeSafetyDeviceStatus(String raw) {
        String value = trim(raw);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String code = extractCode(value);
        if (code == null) {
            code = matchEnumName(SafetyDeviceStatusEnum.values(), value);
        }
        if (code == null || !VALID_SAFETY_STATUS.contains(code)) {
            throw new IllegalArgumentException("安全装置配备情况非法：" + raw + "，可选 1/2/3");
        }
        return code;
    }

    /**
     * 改造类别：模板值为「煤改气」等中文，映射为编码存储（1-煤改电 2-煤改气）
     */
    private String resolveReformType(String raw) {
        String value = trim(raw);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        if ("1".equals(value) || "2".equals(value)) {
            return value;
        }
        if (value.contains("煤改气") || "气代煤".equals(value)) {
            return ReformTypeEnum.GAS_COAL.getType();
        }
        if (value.contains("煤改电") || "电代煤".equals(value)) {
            return ReformTypeEnum.ELECTRIC_COAL.getType();
        }
        return value;
    }

    /**
     * 从「1-xxx」「1」「1.xxx」等字符串中提取开头的数字编码
     */
    private String extractCode(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        char first = value.charAt(0);
        if (Character.isDigit(first)) {
            return String.valueOf(first);
        }
        return null;
    }

    /**
     * 将中文名称匹配为枚举编码
     */
    private String matchEnumName(Enum<?>[] enumValues, String name) {
        for (Enum<?> enumValue : enumValues) {
            try {
                java.lang.reflect.Field nameField = enumValue.getClass().getDeclaredField("name");
                nameField.setAccessible(true);
                String enumName = (String) nameField.get(enumValue);
                if (name.equals(enumName)) {
                    java.lang.reflect.Field typeField = enumValue.getClass().getDeclaredField("type");
                    typeField.setAccessible(true);
                    return (String) typeField.get(enumValue);
                }
            } catch (Exception e) {
                log.warn("解析枚举编码失败: {}", enumValue, e);
            }
        }
        return null;
    }

    private String generateDeviceCode() {
        // 编码规则：GM-6位序号（如 GM-000001），基于库内该前缀最大编码 +1。
        // importRow 每行即时 insert（同一事务内可见自己的插入），下一行查询能拿到上一行编码，逐行递增保证唯一。
        CarbonDeviceDO latest = deviceMapper.selectLatestByCodePrefix(GAS_METER_CODE_PREFIX);
        String lastCode = latest != null ? latest.getDeviceCode() : null;
        return GenerateCode.generateSeqCode(GAS_METER_CODE_PREFIX, lastCode);
    }

    private String generateUserCode() {
        return "USER" + IMPORT_SEQ.incrementAndGet();
    }

    private String trim(String value) {
        return StrUtil.isBlank(value) ? null : value.trim();
    }
}
