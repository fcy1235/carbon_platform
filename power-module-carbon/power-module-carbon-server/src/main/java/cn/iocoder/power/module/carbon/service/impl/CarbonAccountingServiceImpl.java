package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.*;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDevicePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSimpleRespVO;
import cn.iocoder.power.module.carbon.convert.CarbonAccountingConvert;
import cn.iocoder.power.module.carbon.convert.CarbonUserInfoConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingActivityDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonBaselineDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityUsageReportDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasUsageReportDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonAccountingActivityMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonAccountingMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonBaselineMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDeviceDataMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonElectricityUsageReportDataMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasUsageReportMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonAccountingWithUserDTO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoWithAccountingDTO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoWithAccountingMapper;
import cn.iocoder.power.module.carbon.service.CarbonAccountingService;
import cn.iocoder.power.module.carbon.service.CarbonDeviceService;
import cn.iocoder.power.module.carbon.service.CarbonFactorLibService;
import cn.iocoder.power.module.carbon.service.CarbonUserInfoService;
import cn.iocoder.power.module.carbon.util.CarbonUserInfoHelper;
import cn.iocoder.power.module.system.api.dict.DictDataApi;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.ACCOUNTING_NOT_EXISTS;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.ACCOUNTING_PERIOD_EXISTS;
import static cn.iocoder.power.module.infra.enums.DictConstants.CARBON_ACTIVITY_TYPE;

import cn.iocoder.power.module.carbon.enums.ReformTypeEnum;
import cn.iocoder.power.module.carbon.enums.UserDataSourceEnum;

@Service("carbonAccountingService")
@Validated
@Slf4j
public class CarbonAccountingServiceImpl implements CarbonAccountingService {

    @Resource
    private CarbonAccountingMapper accountingMapper;
    @Resource
    private CarbonAccountingActivityMapper activityMapper;
    @Resource
    private CarbonUserInfoWithAccountingMapper userInfoWithAccountingMapper;
    @Resource
    private CarbonUserInfoMapper userInfoMapper;
    @Resource
    private CarbonUserInfoHelper carbonUserInfoHelper;
    @Resource
    private DictDataApi dictDataApi;
    @Resource
    private CarbonBaselineMapper baselineMapper;

    @Resource
    private CarbonDeviceDataMapper deviceDataMapper;

    @Resource
    private CarbonElectricityUsageReportDataMapper electricityUsageReportMapper;

    @Resource
    private CarbonGasUsageReportMapper gasUsageReportMapper;

    @Resource
    private CarbonDeviceService carbonDeviceService;

    @Resource
    private CarbonFactorLibService carbonFactorLibService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccounting(CarbonAccountingSaveReqVO createReqVO) {
        // 校验：同一用户不允许存在相同核算周期的碳排放核算数据
        validateUserPeriodUnique(createReqVO.getCarbonUserInfoId(),
                createReqVO.getAccountingPeriodStart(), createReqVO.getAccountingPeriodEnd(), createReqVO.getId());

        CarbonAccountingDO accounting = CarbonAccountingConvert.INSTANCE.convert(createReqVO);

        List<CarbonAccountingActivityDO> activities = CarbonAccountingConvert.INSTANCE.convertActivityList(createReqVO.getActivities());
        computeEmissions(createReqVO, accounting, activities);

        accountingMapper.insert(accounting);

        if (activities.isEmpty()) {
            return accounting.getId();
        }
        activities.forEach(activity -> activity.setAccountingId(accounting.getId()));
        activityMapper.insertBatch(activities);
        return accounting.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createAccountingBatch(List<CarbonAccountingSaveReqVO> createReqVOList) {
        if (CollUtil.isEmpty(createReqVOList)) {
            return Collections.emptyList();
        }
        // 逐条复用单条创建逻辑（同事务内，任一条失败则整体回滚）
        List<Long> accountingIds = new ArrayList<>(createReqVOList.size());
        for (CarbonAccountingSaveReqVO createReqVO : createReqVOList) {
            accountingIds.add(this.createAccounting(createReqVO));
        }
        return accountingIds;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccounting(CarbonAccountingSaveReqVO updateReqVO) {
        validateAccountingExists(updateReqVO.getId());
        // 校验：同一用户不允许存在相同核算周期的碳排放核算数据（排除自身）
        validateUserPeriodUnique(updateReqVO.getCarbonUserInfoId(),
                updateReqVO.getAccountingPeriodStart(), updateReqVO.getAccountingPeriodEnd(), updateReqVO.getId());

        CarbonAccountingDO accounting = CarbonAccountingConvert.INSTANCE.convert(updateReqVO);
        List<CarbonAccountingActivityDO> activities = CarbonAccountingConvert.INSTANCE.convertActivityList(updateReqVO.getActivities());
        computeEmissions(updateReqVO, accounting, activities);
        accountingMapper.updateById(accounting);

        // 查询历史数据
        List<CarbonAccountingActivityDO> oldActivities = activityMapper.selectListByAccountingId(updateReqVO.getId());

        BiFunction<CarbonAccountingActivityDO, CarbonAccountingActivityDO, Boolean> sameFunc = (origItem, newItem) -> Objects.equals(origItem.getId(), newItem.getId());
        List<List<CarbonAccountingActivityDO>> diffList = CollectionUtils.diffList(oldActivities, activities, sameFunc);
        // 新增
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            activityMapper.insertBatch(diffList.get(0));
        }
        // 修改
        if (!org.springframework.util.CollectionUtils.isEmpty(diffList.get(1))) {
            activityMapper.updateBatch(diffList.get(1));
        }
        // 修改
        if (!org.springframework.util.CollectionUtils.isEmpty(diffList.get(2))) {
            activityMapper.deleteByIds(diffList.get(2));
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccounting(Long id) {
        validateAccountingExists(id);
        activityMapper.deleteByAccountingId(id);
        accountingMapper.deleteById(id);
    }

    @Override
    public CarbonAccountingRespVO getAccounting(Long id) {
        CarbonAccountingDO accounting = accountingMapper.selectById(id);
        if (accounting == null) {
            return null;
        }
        List<CarbonAccountingActivityDO> activities = activityMapper.selectListByAccountingId(id);
        CarbonAccountingRespVO respVO = CarbonAccountingConvert.INSTANCE.convert(accounting);

        // 活动
        respVO.setActivities(CarbonAccountingConvert.INSTANCE.convertActivityRespList(activities));
        // 查询设备信息
        Collection<Long> deviceIds = CollectionUtils.convertSet(activities, CarbonAccountingActivityDO::getCarbonDeviceId);
        Map<Long, CarbonDeviceRespVO> carbonDeviceRespVOMap = carbonDeviceService.getDeviceMap(deviceIds);

        respVO.getActivities().forEach(activity -> {
            CarbonDeviceRespVO device = carbonDeviceRespVOMap.get(activity.getCarbonDeviceId());
            if (device != null) {
                activity.setCarbonDeviceName(device.getDeviceName());
            }
        });
        // 用户信息 行政区、取暖面积
        Map<Long, CarbonUserInfoRespVO> userInfoMap = carbonUserInfoHelper.queryUserInfoMap(CollectionUtils.singleton(respVO.getCarbonUserInfoId()));
        CarbonUserInfoRespVO userInfo = userInfoMap.get(respVO.getCarbonUserInfoId());
        if (userInfo != null) {
            respVO.setUsername(userInfo.getUsername());
            respVO.setDivision(userInfo.getDivision());
            respVO.setAddress(userInfo.getAddress());
            respVO.setHeatingArea(userInfo.getHeatingArea());
            respVO.setReformType(userInfo.getReformType());
            respVO.setDataSource(userInfo.getDataSource());
        }
        // 区域强度、基准线排放量（统一方法，单个也走批量逻辑）
        this.fillBaselineAndEmission(Collections.singletonList(respVO), userInfoMap);
        // 减排量
        BigDecimal baselineEmission = respVO.getBaselineEmission() != null ? respVO.getBaselineEmission() : BigDecimal.ZERO;
        BigDecimal actualEmission = respVO.getActualEmission() != null ? respVO.getActualEmission() : BigDecimal.ZERO;
        respVO.setReduction(baselineEmission.subtract(actualEmission).setScale(3, RoundingMode.HALF_UP));
        // 核算周期内用电量/用气量（根据采暖季和数据源计算）
        this.calculateAndFillHeatingSeasonUsage(Collections.singletonList(respVO));

        return respVO;
    }

    /**
     * 按首末表底计算抄表用量（无记录返回 null；仅一条记录时首末相同，用量为 0）
     */
    private BigDecimal calcUsageByFirstLast(BigDecimal firstTotal, BigDecimal lastTotal) {
        if (firstTotal == null || lastTotal == null) {
            return null;
        }
        return lastTotal.subtract(firstTotal);
    }

    /**
     * 统一填充区域强度和基准线排放量（支持单个和批量）
     *
     * @param list        核算列表
     * @param userInfoMap 用户信息 Map
     */
    private void fillBaselineAndEmission(List<CarbonAccountingRespVO> list, Map<Long, CarbonUserInfoRespVO> userInfoMap) {
        if (CollUtil.isEmpty(list) || CollUtil.isEmpty(userInfoMap)) {
            return;
        }

        // 收集需要查询 baseline 的省市区组合
        Set<Long> provinceCodes = new HashSet<>();
        Set<Long> cityCodes = new HashSet<>();
        Set<Long> districtCodes = new HashSet<>();
        for (CarbonAccountingRespVO item : list) {
            CarbonUserInfoRespVO userInfo = userInfoMap.get(item.getCarbonUserInfoId());
            if (userInfo != null && userInfo.getProvinceCode() != null && userInfo.getCityCode() != null && userInfo.getDistrictCode() != null) {
                provinceCodes.add(userInfo.getProvinceCode());
                cityCodes.add(userInfo.getCityCode());
                districtCodes.add(userInfo.getDistrictCode());
            }
        }

        if (CollUtil.isEmpty(provinceCodes)) {
            return;
        }

        // 批量查询区域强度（核算场景只需 intensity，直接查 Mapper，跳过行政区名称等展示性填充）
        CarbonBaselinePageReqVO baselineReq = new CarbonBaselinePageReqVO();
        baselineReq.setProvinceCodes(provinceCodes);
        baselineReq.setCityCodes(cityCodes);
        baselineReq.setDistrictCodes(districtCodes);
        List<CarbonBaselineDO> baselineList = baselineMapper.selectList(baselineReq);

        Map<String, CarbonBaselineDO> baselineMap = baselineList.stream()
                .collect(Collectors.toMap(
                        b -> b.getProvinceCode() + "-" + b.getCityCode() + "-" + b.getDistrictCode(),
                        b -> b,
                        (a, b) -> a
                ));

        for (CarbonAccountingRespVO item : list) {
            CarbonUserInfoRespVO userInfo = userInfoMap.get(item.getCarbonUserInfoId());
            if (userInfo == null || userInfo.getProvinceCode() == null || userInfo.getCityCode() == null || userInfo.getDistrictCode() == null) {
                continue;
            }
            String key = userInfo.getProvinceCode() + "-" + userInfo.getCityCode() + "-" + userInfo.getDistrictCode();
            CarbonBaselineDO baseline = baselineMap.get(key);
            if (baseline != null) {
                item.setBaselineIntensity(baseline.getIntensity());
                BigDecimal heatingArea = userInfo.getHeatingArea() != null ? userInfo.getHeatingArea() : BigDecimal.ZERO;
                item.setBaselineEmission(heatingArea.multiply(baseline.getIntensity()));
            }
        }
    }

    @Override
    public List<CarbonAccountingRespVO> getAccountingList(CarbonAccountingPageReqVO pageReqVO) {
        // 连表查询：碳核算 + 用户信息 + 活动信息
        List<CarbonAccountingWithUserDTO> dtoList = accountingMapper.selectListWithUser(pageReqVO);
        if (dtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // 聚合活动信息并转换为 RespVO
        List<CarbonAccountingRespVO> list = aggregateAndConvert(dtoList);

        // 填充行政区划名称
        carbonUserInfoHelper.fillDivision(list,
                CarbonAccountingRespVO::getProvinceCode,
                CarbonAccountingRespVO::getCityCode,
                CarbonAccountingRespVO::getDistrictCode,
                CarbonAccountingRespVO::setDivision);

        // 填充基准线排放量
        Map<Long, CarbonUserInfoRespVO> userInfoMap = buildUserInfoMap(dtoList);
        this.fillBaselineAndEmission(list, userInfoMap);

        // 核算周期内用电量/用气量（采暖季批量计算）
        this.calculateAndFillHeatingSeasonUsage(list);

        return list;
    }

    /**
     * 聚合活动信息并转换为 RespVO
     * 由于 LEFT JOIN 活动表，一条碳核算记录可能对应多条活动记录，需要按碳核算ID聚合
     */
    private List<CarbonAccountingRespVO> aggregateAndConvert(List<CarbonAccountingWithUserDTO> dtoList) {
        // 获取活动名称字典
        CommonResult<List<DictDataRespDTO>> dictDataResult = dictDataApi.getDictDataList(CARBON_ACTIVITY_TYPE);
        List<DictDataRespDTO> dictList = dictDataResult.getData();
        Map<String, DictDataRespDTO> dictMap = CollectionUtils.convertMap(dictList, DictDataRespDTO::getValue);

        // 按碳核算ID分组聚合活动信息
        Map<Long, List<CarbonAccountingWithUserDTO>> groupedMap = dtoList.stream()
                .collect(Collectors.groupingBy(CarbonAccountingWithUserDTO::getId));

        List<CarbonAccountingRespVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<CarbonAccountingWithUserDTO>> entry : groupedMap.entrySet()) {
            List<CarbonAccountingWithUserDTO> groupList = entry.getValue();
            CarbonAccountingWithUserDTO firstDto = groupList.get(0);

            CarbonAccountingRespVO respVO = convertToRespVO(firstDto);

            // 聚合活动名称编码
            Set<String> activityNameCodes = groupList.stream()
                    .map(CarbonAccountingWithUserDTO::getActivityNameCode)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());

            // 设置活动编码和活动名称
            String activityCodes = String.join("、", activityNameCodes);
            respVO.setActivityCodes(activityCodes);

            String activityNames = activityNameCodes.stream()
                    .filter(dictMap::containsKey)
                    .map(code -> dictMap.get(code).getLabel())
                    .collect(Collectors.joining("、"));
            respVO.setActivityName(activityNames);

            result.add(respVO);
        }
        return result;
    }

    /**
     * 构建用户信息 Map
     */
    private Map<Long, CarbonUserInfoRespVO> buildUserInfoMap(List<CarbonAccountingWithUserDTO> dtoList) {
        Map<Long, CarbonUserInfoRespVO> userInfoMap = new HashMap<>();
        for (CarbonAccountingWithUserDTO dto : dtoList) {
            if (dto.getCarbonUserInfoId() != null && !userInfoMap.containsKey(dto.getCarbonUserInfoId())) {
                CarbonUserInfoRespVO userInfo = new CarbonUserInfoRespVO();
                userInfo.setId(dto.getCarbonUserInfoId());
                userInfo.setProvinceCode(dto.getProvinceCode());
                userInfo.setCityCode(dto.getCityCode());
                userInfo.setDistrictCode(dto.getDistrictCode());
                userInfo.setHeatingArea(dto.getHeatingArea());
                userInfoMap.put(dto.getCarbonUserInfoId(), userInfo);
            }
        }
        return userInfoMap;
    }

    /**
     * 将连表查询结果转换为 RespVO
     */
    private CarbonAccountingRespVO convertToRespVO(CarbonAccountingWithUserDTO dto) {
        CarbonAccountingRespVO respVO = new CarbonAccountingRespVO();
        respVO.setId(dto.getId());
        respVO.setCarbonUserInfoId(dto.getCarbonUserInfoId());
        respVO.setAccountingPeriodStart(dto.getAccountingPeriodStart() != null ? dto.getAccountingPeriodStart().atStartOfDay() : null);
        respVO.setAccountingPeriodEnd(dto.getAccountingPeriodEnd() != null ? dto.getAccountingPeriodEnd().atStartOfDay() : null);
        respVO.setActualEmission(dto.getActualEmission());
        respVO.setReduction(dto.getReduction());
        respVO.setCreateTime(dto.getCreateTime());
        // 用户信息
        respVO.setUsername(dto.getUsername());
        respVO.setAddress(dto.getAddress());
        respVO.setHeatingArea(dto.getHeatingArea());
        respVO.setReformType(dto.getReformType());
        respVO.setUserCode(dto.getUserCode());
        respVO.setDataSource(dto.getDataSource());
        // 保存省市区编码（用于后续填充行政区划名称）
        respVO.setProvinceCode(dto.getProvinceCode());
        respVO.setCityCode(dto.getCityCode());
        respVO.setDistrictCode(dto.getDistrictCode());
        return respVO;
    }

    private void setCarbonUserInfoHelper(List<CarbonAccountingRespVO> list, List<CarbonUserInfoRespVO> userInfoList) {
        if (!list.isEmpty()) {
            Map<Long, CarbonUserInfoRespVO> userInfoMap = carbonUserInfoHelper.queryUserInfoMap(
                    CollectionUtils.convertSet(userInfoList, CarbonUserInfoRespVO::getId));
            carbonUserInfoHelper.fillUserInfo(list, userInfoMap,
                    CarbonAccountingRespVO::getCarbonUserInfoId,
                    CarbonAccountingRespVO::setUsername,
                    CarbonAccountingRespVO::setDivision,
                    CarbonAccountingRespVO::setAddress
            );
            list.forEach(e -> e.setReformType(userInfoMap.getOrDefault(e.getCarbonUserInfoId(), new CarbonUserInfoRespVO()).getReformType()));
        }
    }

    @Override
    public List<CarbonAccountingActivityDO> getAccountingActivities(Long accountingId) {
        return activityMapper.selectListByAccountingId(accountingId);
    }

    @Override
    public CarbonAccountingRespVO calculateReduction(CarbonAccountingCalculateReqVO reqVO) {
        CarbonAccountingRespVO result = new CarbonAccountingRespVO();
        result.setHeatingArea(reqVO.getHeatingArea());
        result.setBaselineIntensity(reqVO.getBaselineIntensity());

        BigDecimal heatingArea = reqVO.getHeatingArea() != null ? reqVO.getHeatingArea() : BigDecimal.ZERO;
        BigDecimal baselineIntensity = reqVO.getBaselineIntensity() != null ? reqVO.getBaselineIntensity() : BigDecimal.ZERO;
        BigDecimal baselineEmission = heatingArea.multiply(baselineIntensity);

        BigDecimal actualEmission = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(reqVO.getActivities())) {
            actualEmission = reqVO.getActivities().stream()
                    .map(a -> {
                        BigDecimal level = a.getActivityLevel() != null ? a.getActivityLevel() : BigDecimal.ZERO;
                        BigDecimal factor = a.getEmissionFactor() != null ? a.getEmissionFactor() : BigDecimal.ZERO;
                        return level.multiply(factor);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        result.setBaselineEmission(baselineEmission);
        result.setActualEmission(actualEmission);
        result.setReduction(baselineEmission.subtract(actualEmission).setScale(3, RoundingMode.HALF_UP));
        return result;
    }

    @Override
    public PageResult<CarbonAccountUserInfoRespVO> getAccountUserInfoPage(CarbonAccountingPageReqVO pageReqVO) {
        // 连表查询：碳核算（主表） + 用户信息（关联表）
        PageResult<CarbonUserInfoWithAccountingDTO> pageResult = userInfoWithAccountingMapper.selectPageWithAccounting(pageReqVO);
        if (pageResult.getList().isEmpty()) {
            return PageResult.empty();
        }

        // 转换为 RespVO
        List<CarbonAccountUserInfoRespVO> list = pageResult.getList().stream()
                .map(this::convertToAccountUserInfoRespVO)
                .collect(Collectors.toList());

        // 填充行政区划名称
        carbonUserInfoHelper.fillDivision(list,
                CarbonAccountUserInfoRespVO::getProvinceCode,
                CarbonAccountUserInfoRespVO::getCityCode,
                CarbonAccountUserInfoRespVO::getDistrictCode,
                CarbonAccountUserInfoRespVO::setDivision);

        // 构建 userInfoMap 用于填充基准线排放量
        Map<Long, CarbonUserInfoRespVO> userInfoMap = new HashMap<>();
        for (CarbonUserInfoWithAccountingDTO dto : pageResult.getList()) {
            CarbonUserInfoRespVO userInfo = new CarbonUserInfoRespVO();
            userInfo.setId(dto.getCarbonUserInfoId());
            userInfo.setProvinceCode(dto.getProvinceCode());
            userInfo.setCityCode(dto.getCityCode());
            userInfo.setDistrictCode(dto.getDistrictCode());
            userInfo.setHeatingArea(dto.getHeatingArea());
            userInfoMap.put(dto.getCarbonUserInfoId(), userInfo);
        }

        // 区域强度、基准线排放量
        this.fillBaselineAndEmissionForAccountUserInfo(list, userInfoMap);

        // 核算周期内用电量/用气量（采暖季批量计算）
        this.fillHeatingSeasonUsageForUserInfo(list);

        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PageResult<CarbonAccountUserInfoRespVO> getUnaccountedUserPage(CarbonAccountingPageReqVO pageReqVO) {
        // 排除已核算用户依赖核算周期，任一为空则直接报错（缺失会导致子查询日期比较异常）
        if (ObjectUtil.isEmpty(pageReqVO.getAccountingPeriodStart())
                || ObjectUtil.isEmpty(pageReqVO.getAccountingPeriodEnd())) {
            throw new IllegalArgumentException("核算周期开始时间和结束时间不能为空");
        }
        // 1. 计算取暖季年份
        String heatingSeason = determineHeatingSeason(pageReqVO.getAccountingPeriodStart());

        // 2. 从三个数据表查询在该取暖季有数据的用户ID集合
        Set<Long> userIdsWithDataSet = new HashSet<>();

        // 2.1 从 carbon_electricity_usage_report 查询
        List<Long> electricityUserIds = electricityUsageReportMapper.selectDistinctUserIdsByHeatingSeason(heatingSeason);
        if (CollUtil.isNotEmpty(electricityUserIds)) {
            userIdsWithDataSet.addAll(electricityUserIds);
        }
        // 2.2 从 carbon_gas_usage_report 查询
        List<Long> gasUserIds = gasUsageReportMapper.selectDistinctUserIdsByHeatingSeason(heatingSeason);
        if (CollUtil.isNotEmpty(gasUserIds)) {
            userIdsWithDataSet.addAll(gasUserIds);
        }


        // 2.3 从 carbon_device_data 查询（按取暖季时间范围）
        LocalDate seasonStart = LocalDate.of(Integer.parseInt(heatingSeason), 11, 1);
        LocalDate seasonEnd = LocalDate.of(Integer.parseInt(heatingSeason) + 1, 3, 31);
        LocalDateTime seasonStartDateTime = seasonStart.atStartOfDay();
        LocalDateTime seasonEndDateTime = seasonEnd.atTime(23, 59, 59);
        List<Long> deviceUserIds = deviceDataMapper.selectDistinctUserIdsByTimeRange(seasonStartDateTime, seasonEndDateTime);
        if (CollUtil.isNotEmpty(deviceUserIds)) {
            userIdsWithDataSet.addAll(deviceUserIds);
        }


        // 3. 转换为列表
        List<Long> userIdsWithData = new ArrayList<>(userIdsWithDataSet);

        // 4. 分页查询用户信息（只查询有数据且未核算的用户）
        PageResult<CarbonUserInfoDO> pageResult = userInfoMapper.selectPageUnaccounted(pageReqVO, userIdsWithData);
        if (pageResult.getList().isEmpty()) {
            return PageResult.empty();
        }

        // DO -> RespVO（CarbonAccountUserInfoRespVO 兼容核算相关字段，此处核算字段为空）
        List<CarbonAccountUserInfoRespVO> list = pageResult.getList().stream()
                .map(CarbonUserInfoConvert.INSTANCE::convert)
                .map(CarbonAccountingConvert.INSTANCE::convert)
                .collect(Collectors.toList());
        // 回填用户编号与目标核算周期，供前端展示与后续填充使用
        list.forEach(vo -> {
            vo.setCarbonUserInfoId(vo.getId());
            vo.setAccountingPeriodStart(pageReqVO.getAccountingPeriodStart());
            vo.setAccountingPeriodEnd(pageReqVO.getAccountingPeriodEnd());
        });


        // 填充行政区划名称
        carbonUserInfoHelper.fillDivision(list,
                CarbonAccountUserInfoRespVO::getProvinceCode,
                CarbonAccountUserInfoRespVO::getCityCode,
                CarbonAccountUserInfoRespVO::getDistrictCode,
                CarbonAccountUserInfoRespVO::setDivision);


        // 构建 userInfoMap 用于填充区域基准碳排放强度、基准线排放量（批量新增按行政区带出默认强度）
        Map<Long, CarbonUserInfoRespVO> userInfoMap = new HashMap<>();
        for (CarbonUserInfoDO user : pageResult.getList()) {
            CarbonUserInfoRespVO userInfo = new CarbonUserInfoRespVO();
            userInfo.setId(user.getId());
            userInfo.setProvinceCode(user.getProvinceCode());
            userInfo.setCityCode(user.getCityCode());
            userInfo.setDistrictCode(user.getDistrictCode());
            userInfo.setHeatingArea(user.getHeatingArea());
            userInfoMap.put(user.getId(), userInfo);
        }
        this.fillBaselineAndEmissionForAccountUserInfo(list, userInfoMap);

        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PageResult<CarbonAccountUserInfoRespVO> getAccountingPage(CarbonAccountingPageReqVO pageReqVO) {
        // 连表查询：碳核算（主表） + 用户信息（关联表），只返回能关联到用户的碳核算记录
        PageResult<CarbonUserInfoWithAccountingDTO> pageResult = userInfoWithAccountingMapper.selectPageWithAccountingOnly(pageReqVO);
        if (pageResult.getList().isEmpty()) {
            return PageResult.empty();
        }

        // 转换为 RespVO
        List<CarbonAccountUserInfoRespVO> list =  CollectionUtils.convertList(pageResult.getList(), this::convertToAccountUserInfoRespVO);

        // 填充行政区划名称
        carbonUserInfoHelper.fillDivision(list,
                CarbonAccountUserInfoRespVO::getProvinceCode,
                CarbonAccountUserInfoRespVO::getCityCode,
                CarbonAccountUserInfoRespVO::getDistrictCode,
                CarbonAccountUserInfoRespVO::setDivision);

        // 构建 userInfoMap 用于填充基准线排放量
        Map<Long, CarbonUserInfoRespVO> userInfoMap = new HashMap<>();
        for (CarbonUserInfoWithAccountingDTO dto : pageResult.getList()) {
            CarbonUserInfoRespVO userInfo = new CarbonUserInfoRespVO();
            userInfo.setId(dto.getCarbonUserInfoId());
            userInfo.setProvinceCode(dto.getProvinceCode());
            userInfo.setCityCode(dto.getCityCode());
            userInfo.setDistrictCode(dto.getDistrictCode());
            userInfo.setHeatingArea(dto.getHeatingArea());
            userInfoMap.put(dto.getCarbonUserInfoId(), userInfo);
        }

        // 区域强度、基准线排放量
        this.fillBaselineAndEmissionForAccountUserInfo(list, userInfoMap);

        // 填充活动名称
        fillActivityNames(list);

        // 采暖季用电量/用气量（按 reformType/dataSource 路由到对应数据源，批量预加载后计算）
        fillHeatingSeasonUsageForUserInfo(list);

        return new PageResult<>(list, pageResult.getTotal());
    }

    /**
     * 填充活动名称（多个活动用顿号隔开）
     */
    private void fillActivityNames(List<CarbonAccountUserInfoRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }

        // 获取活动名称字典
        CommonResult<List<DictDataRespDTO>> dictDataResult = dictDataApi.getDictDataList(CARBON_ACTIVITY_TYPE);
        List<DictDataRespDTO> dictList = dictDataResult.getData();
        Map<String, DictDataRespDTO> dictMap = CollectionUtils.convertMap(dictList, DictDataRespDTO::getValue);

        // 收集所有碳核算ID
        List<Long> accountingIds = list.stream()
                .map(CarbonAccountUserInfoRespVO::getAccountingId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(accountingIds)) {
            return;
        }

        // 批量查询活动信息
        List<CarbonAccountingActivityDO> allActivities = activityMapper.selectListByAccountingIds(accountingIds);

        // 按碳核算ID分组
        Map<Long, List<CarbonAccountingActivityDO>> activityMap = allActivities.stream()
                .collect(Collectors.groupingBy(CarbonAccountingActivityDO::getAccountingId));

        // 填充活动名称
        for (CarbonAccountUserInfoRespVO item : list) {
            List<CarbonAccountingActivityDO> activities = activityMap.get(item.getAccountingId());
            if (CollUtil.isEmpty(activities)) {
                continue;
            }

            // 聚合活动名称编码
            Set<String> activityNameCodes = activities.stream()
                    .map(CarbonAccountingActivityDO::getActivityNameCode)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());

            // 转换为活动名称
            String activityNames = activityNameCodes.stream()
                    .filter(dictMap::containsKey)
                    .map(code -> dictMap.get(code).getLabel())
                    .collect(Collectors.joining("、"));

            item.setActivityName(activityNames);
        }
    }

    /**
     * 将连表查询结果转换为 CarbonAccountUserInfoRespVO
     */
    private CarbonAccountUserInfoRespVO convertToAccountUserInfoRespVO(CarbonUserInfoWithAccountingDTO dto) {
        CarbonAccountUserInfoRespVO respVO = new CarbonAccountUserInfoRespVO();
        // 用户信息（id 字段为碳核算编号，用户编号放 carbonUserInfoId）
        respVO.setId(dto.getId());
        respVO.setCarbonUserInfoId(dto.getCarbonUserInfoId());
        respVO.setUsername(dto.getUsername());
        respVO.setUserCode(dto.getUserCode());
        respVO.setIdCard(dto.getIdCard());
        respVO.setPhone(dto.getPhone());
        respVO.setProvinceCode(dto.getProvinceCode());
        respVO.setCityCode(dto.getCityCode());
        respVO.setDistrictCode(dto.getDistrictCode());
        respVO.setTownCode(dto.getTownCode());
        respVO.setVillageCode(dto.getVillageCode());
        respVO.setAddress(dto.getAddress());
        respVO.setHeatingArea(dto.getHeatingArea());
        respVO.setReformType(dto.getReformType());
        respVO.setReformMode(dto.getReformMode());
        respVO.setDataSource(dto.getDataSource());
        respVO.setElectricityId(dto.getElectricityId());
        respVO.setGasId(dto.getGasId());
        respVO.setReformYear(dto.getReformYear());
        respVO.setReformBatch(dto.getReformBatch());
        respVO.setSubsidyMethod(dto.getSubsidyMethod());
        respVO.setHouseUsage(dto.getHouseUsage());
        respVO.setUserCategory(dto.getUserCategory());
        respVO.setGasUserCode(dto.getGasUserCode());
        respVO.setUseStatus(dto.getUseStatus());
        respVO.setRemark(dto.getRemark());
        respVO.setCreateTime(dto.getCreateTime());
        // 碳核算信息
        respVO.setAccountingId(dto.getId());
        respVO.setAccountingPeriodStart(dto.getAccountingPeriodStart());
        respVO.setAccountingPeriodEnd(dto.getAccountingPeriodEnd());
        respVO.setActualEmission(dto.getActualEmission());
        respVO.setReduction(dto.getReduction());
        return respVO;
    }

    private void fillBaselineAndEmissionForAccountUserInfo(List<CarbonAccountUserInfoRespVO> list, Map<Long, CarbonUserInfoRespVO> userInfoMap) {
        if (CollUtil.isEmpty(list) || CollUtil.isEmpty(userInfoMap)) {
            return;
        }

        Set<Long> provinceCodes = new HashSet<>();
        Set<Long> cityCodes = new HashSet<>();
        Set<Long> districtCodes = new HashSet<>();
        for (CarbonAccountUserInfoRespVO item : list) {
            CarbonUserInfoRespVO userInfo = userInfoMap.get(item.getCarbonUserInfoId());
            if (userInfo != null && userInfo.getProvinceCode() != null && userInfo.getCityCode() != null && userInfo.getDistrictCode() != null) {
                provinceCodes.add(userInfo.getProvinceCode());
                cityCodes.add(userInfo.getCityCode());
                districtCodes.add(userInfo.getDistrictCode());
            }
        }

        if (CollUtil.isEmpty(provinceCodes)) {
            return;
        }

        CarbonBaselinePageReqVO baselineReq = new CarbonBaselinePageReqVO();
        baselineReq.setProvinceCodes(provinceCodes);
        baselineReq.setCityCodes(cityCodes);
        baselineReq.setDistrictCodes(districtCodes);
        List<CarbonBaselineDO> baselineList = baselineMapper.selectList(baselineReq);

        Map<String, CarbonBaselineDO> baselineMap = baselineList.stream()
                .collect(Collectors.toMap(
                        b -> b.getProvinceCode() + "-" + b.getCityCode() + "-" + b.getDistrictCode(),
                        b -> b,
                        (a, b) -> a
                ));

        for (CarbonAccountUserInfoRespVO item : list) {
            CarbonUserInfoRespVO userInfo = userInfoMap.get(item.getCarbonUserInfoId());
            if (userInfo == null || userInfo.getProvinceCode() == null || userInfo.getCityCode() == null || userInfo.getDistrictCode() == null) {
                continue;
            }
            String key = userInfo.getProvinceCode() + "-" + userInfo.getCityCode() + "-" + userInfo.getDistrictCode();
            CarbonBaselineDO baseline = baselineMap.get(key);
            if (baseline != null) {
                item.setBaselineIntensity(baseline.getIntensity());
                BigDecimal heatingArea = userInfo.getHeatingArea() != null ? userInfo.getHeatingArea() : BigDecimal.ZERO;
                item.setBaselineEmission(heatingArea.multiply(baseline.getIntensity()));
            }
        }
    }



    private void computeEmissions(CarbonAccountingSaveReqVO reqVO, CarbonAccountingDO accounting, List<CarbonAccountingActivityDO> activities) {
//        BigDecimal heatingArea = reqVO.getHeatingArea() != null ? reqVO.getHeatingArea() : BigDecimal.ZERO;
//        BigDecimal baselineIntensity = reqVO.getBaselineIntensity() != null ? reqVO.getBaselineIntensity() : BigDecimal.ZERO;
        BigDecimal baselineEmission = reqVO.getBaselineEmission();

        BigDecimal actualEmission = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(activities)) {
            for (CarbonAccountingActivityDO activity : activities) {
                BigDecimal level = activity.getActivityLevel() != null ? activity.getActivityLevel() : BigDecimal.ZERO;
                BigDecimal factor = activity.getEmissionFactor() != null ? activity.getEmissionFactor() : BigDecimal.ZERO;
                activity.setEmission(level.multiply(factor).setScale(3, RoundingMode.HALF_UP));
                actualEmission = actualEmission.add(activity.getEmission());
            }
        }

        accounting.setActualEmission(actualEmission);
        BigDecimal reduction = baselineEmission.subtract(actualEmission).setScale(3, RoundingMode.HALF_UP);
        accounting.setReduction(reduction);
    }

    /**
     * 校验同一用户不允许存在相同核算周期的碳排放核算数据
     *
     * @param carbonUserInfoId       用户信息编号
     * @param accountingPeriodStart  核算周期开始日期
     * @param accountingPeriodEnd    核算周期结束日期
     * @param id                     碳核算编号（更新时用于排除自身，新增时为 null）
     */
    private void validateUserPeriodUnique(Long carbonUserInfoId, LocalDate accountingPeriodStart,
                                          LocalDate accountingPeriodEnd, Long id) {
        if (carbonUserInfoId == null || accountingPeriodStart == null || accountingPeriodEnd == null) {
            return;
        }
        Long count = accountingMapper.selectCountByUserInfoIdAndPeriod(carbonUserInfoId,
                accountingPeriodStart, accountingPeriodEnd, id);
        if (count != null && count > 0) {
            throw exception(ACCOUNTING_PERIOD_EXISTS);
        }
    }

    @Override
    public void calculateAndFillHeatingSeasonUsage(List<CarbonAccountingRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<UsageContext> contexts = new ArrayList<>(list.size());
        for (CarbonAccountingRespVO vo : list) {
            if (vo.getCarbonUserInfoId() == null || vo.getAccountingPeriodStart() == null || vo.getAccountingPeriodEnd() == null) {
                continue;
            }
            UsageContext ctx = new UsageContext();
            ctx.userId = vo.getCarbonUserInfoId();
            ctx.reformType = vo.getReformType();
            ctx.dataSource = vo.getDataSource();
            ctx.userCode = vo.getUserCode();
            ctx.periodStart = vo.getAccountingPeriodStart().toLocalDate();
            ctx.periodEnd = vo.getAccountingPeriodEnd().toLocalDate();
            ctx.electricitySetter = vo::setElectricityUsage;
            ctx.gasSetter = vo::setGasUsage;
            contexts.add(ctx);
        }
        doBatchFillHeatingSeasonUsage(contexts);
    }

    /**
     * 批量填充采暖季用电量/用气量（CarbonAccountUserInfoRespVO 版本，用于分页列表场景）
     */
    private void fillHeatingSeasonUsageForUserInfo(List<CarbonAccountUserInfoRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<UsageContext> contexts = new ArrayList<>(list.size());
        for (CarbonAccountUserInfoRespVO vo : list) {
            if (vo.getCarbonUserInfoId() == null || vo.getAccountingPeriodStart() == null || vo.getAccountingPeriodEnd() == null) {
                continue;
            }
            UsageContext ctx = new UsageContext();
            ctx.userId = vo.getCarbonUserInfoId();
            ctx.reformType = vo.getReformType();
            ctx.dataSource = vo.getDataSource();
            ctx.userCode = vo.getUserCode();
            ctx.periodStart = vo.getAccountingPeriodStart();
            ctx.periodEnd = vo.getAccountingPeriodEnd();
            ctx.electricitySetter = vo::setElectricityUsage;
            ctx.gasSetter = vo::setGasUsage;
            contexts.add(ctx);
        }
        doBatchFillHeatingSeasonUsage(contexts);
    }

    /**
     * 采暖季用电量/用气量批量计算核心逻辑
     *
     * 先按 reformType 分组批量预加载数据，再统一计算填充，避免循环内逐条查库。
     *
     * 计算规则：
     * - 煤改电 + 接口数据：取 carbon_device_data 核算周期内首末 totalElectricity 差值
     * - 煤改电 + 导入数据：取 carbon_electricity_usage_report 按采暖季查询首末表底差或 totalUsage
     * - 煤改气：取 carbon_gas_usage_report 按采暖季查询首末表底差或 totalUsage
     */
    private void doBatchFillHeatingSeasonUsage(List<UsageContext> items) {
        if (CollUtil.isEmpty(items)) {
            return;
        }

        // 1. 按 reformType 分组
        List<UsageContext> electricCoalItems = new ArrayList<>();
        List<UsageContext> gasCoalItems = new ArrayList<>();
        for (UsageContext item : items) {
            if (ReformTypeEnum.ELECTRIC_COAL.getType().equals(item.reformType)) {
                electricCoalItems.add(item);
            } else if (ReformTypeEnum.GAS_COAL.getType().equals(item.reformType)) {
                gasCoalItems.add(item);
            }
        }

        // 2. 煤改电：按 dataSource 再分组，批量预加载后计算
        if (CollUtil.isNotEmpty(electricCoalItems)) {
            List<UsageContext> interfaceItems = new ArrayList<>();
            List<UsageContext> importItems = new ArrayList<>();
            for (UsageContext item : electricCoalItems) {
                if (UserDataSourceEnum.INTERFACE_DATA.getType().equals(item.dataSource)) {
                    interfaceItems.add(item);
                } else {
                    importItems.add(item);
                }
            }
            batchFillElectricityFromDeviceData(interfaceItems);
            batchFillElectricityFromUsageReport(importItems);
        }

        // 3. 煤改气：批量预加载用气量报表后计算
        if (CollUtil.isNotEmpty(gasCoalItems)) {
            batchFillGasFromUsageReport(gasCoalItems);
        }
    }

    /**
     * 批量从 carbon_device_data 计算用电量（煤改电 + 接口数据）
     * 按核算周期分组，每组一次查询拿到全部用户设备数据，再按 userCode 归类计算
     */
    private void batchFillElectricityFromDeviceData(List<UsageContext> items) {
        if (CollUtil.isEmpty(items)) {
            return;
        }
        // 按核算周期分组（同周期共享一次查询）
        Map<String, List<UsageContext>> byPeriod = items.stream()
                .collect(Collectors.groupingBy(ctx -> ctx.periodStart + "|" + ctx.periodEnd));
        for (List<UsageContext> group : byPeriod.values()) {
            Set<String> userCodes = group.stream()
                    .map(ctx -> ctx.userCode).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
            if (CollUtil.isEmpty(userCodes)) {
                continue;
            }
            LocalDateTime start = group.get(0).periodStart.atStartOfDay();
            LocalDateTime end = group.get(0).periodEnd.atTime(LocalTime.MAX);
            List<CarbonDeviceDataDO> allRecords = deviceDataMapper.selectListByUserCodesAndTimeRange(userCodes, start, end);
            Map<String, List<CarbonDeviceDataDO>> byUserCode = allRecords.stream()
                    .filter(r -> r.getUserCode() != null)
                    .collect(Collectors.groupingBy(CarbonDeviceDataDO::getUserCode));
            for (UsageContext ctx : group) {
                if (StrUtil.isBlank(ctx.userCode)) {
                    continue;
                }
                ctx.electricitySetter.accept(calcUsageFromDeviceRecords(byUserCode.get(ctx.userCode)));
            }
        }
    }

    /**
     * 批量从 carbon_electricity_usage_report 计算用电量（煤改电 + 导入数据）
     * 按采暖季分组，每季一次查询拿到全部用户报表记录，再按 userId 归类计算
     */
    private void batchFillElectricityFromUsageReport(List<UsageContext> items) {
        if (CollUtil.isEmpty(items)) {
            return;
        }
        Map<String, List<UsageContext>> bySeason = items.stream()
                .collect(Collectors.groupingBy(ctx -> determineHeatingSeason(ctx.periodStart)));
        for (Map.Entry<String, List<UsageContext>> entry : bySeason.entrySet()) {
            Set<Long> userIds = entry.getValue().stream().map(ctx -> ctx.userId).collect(Collectors.toSet());
            List<CarbonElectricityUsageReportDataDO> records = electricityUsageReportMapper
                    .selectListByUserInfoIdsAndHeatingSeason(userIds, entry.getKey());
            Map<Long, List<CarbonElectricityUsageReportDataDO>> byUser = records.stream()
                    .collect(Collectors.groupingBy(CarbonElectricityUsageReportDataDO::getCarbonUserInfoId));
            for (UsageContext ctx : entry.getValue()) {
                ctx.electricitySetter.accept(calcUsageFromElectricityRecords(byUser.get(ctx.userId)));
            }
        }
    }

    /**
     * 批量从 carbon_gas_usage_report 计算用气量（煤改气）
     * 按采暖季分组，每季一次查询拿到全部用户报表记录，再按 userId 归类计算
     */
    private void batchFillGasFromUsageReport(List<UsageContext> items) {
        if (CollUtil.isEmpty(items)) {
            return;
        }
        Map<String, List<UsageContext>> bySeason = items.stream()
                .collect(Collectors.groupingBy(ctx -> determineHeatingSeason(ctx.periodStart)));
        for (Map.Entry<String, List<UsageContext>> entry : bySeason.entrySet()) {
            Set<Long> userIds = entry.getValue().stream().map(ctx -> ctx.userId).collect(Collectors.toSet());
            List<CarbonGasUsageReportDO> records = gasUsageReportMapper
                    .selectListByUserInfoIdsAndHeatingSeason(userIds, entry.getKey());
            Map<Long, List<CarbonGasUsageReportDO>> byUser = records.stream()
                    .collect(Collectors.groupingBy(CarbonGasUsageReportDO::getCarbonUserInfoId));
            for (UsageContext ctx : entry.getValue()) {
                ctx.gasSetter.accept(calcUsageFromGasRecords(byUser.get(ctx.userId)));
            }
        }
    }

    /**
     * 根据设备数据记录计算用电量：末条 totalElectricity - 首条 totalElectricity
     */
    private BigDecimal calcUsageFromDeviceRecords(List<CarbonDeviceDataDO> records) {
        if (CollUtil.isEmpty(records)) {
            return null;
        }
        BigDecimal firstTotal = records.get(0).getTotalElectricity();
        BigDecimal lastTotal = records.get(records.size() - 1).getTotalElectricity();
        if (firstTotal == null || lastTotal == null) {
            return null;
        }
        return lastTotal.subtract(firstTotal);
    }

    /**
     * 根据用电量报表记录计算用电量
     * 仅一条且 totalUsage 非空时直接取 totalUsage；多条时取末条 endReading - 首条 startReading
     */
    private BigDecimal calcUsageFromElectricityRecords(List<CarbonElectricityUsageReportDataDO> records) {
        if (CollUtil.isEmpty(records)) {
            return null;
        }
        if (records.size() == 1) {
            CarbonElectricityUsageReportDataDO single = records.get(0);
            if (single.getTotalUsage() != null) {
                return single.getTotalUsage();
            }
            return calcUsageByFirstLast(single.getStartReading(), single.getEndReading());
        }
        BigDecimal firstStart = records.get(0).getStartReading();
        BigDecimal lastEnd = records.get(records.size() - 1).getEndReading();
        return calcUsageByFirstLast(firstStart, lastEnd);
    }

    /**
     * 根据用气量报表记录计算用气量
     * 仅一条且 totalUsage 非空时直接取 totalUsage；多条时取末条 endReading - 首条 startReading
     */
    private BigDecimal calcUsageFromGasRecords(List<CarbonGasUsageReportDO> records) {
        if (CollUtil.isEmpty(records)) {
            return null;
        }
        if (records.size() == 1) {
            CarbonGasUsageReportDO single = records.get(0);
            if (single.getTotalUsage() != null) {
                return single.getTotalUsage();
            }
            return calcUsageByFirstLast(single.getStartReading(), single.getEndReading());
        }
        BigDecimal firstStart = records.get(0).getStartReading();
        BigDecimal lastEnd = records.get(records.size() - 1).getEndReading();
        return calcUsageByFirstLast(firstStart, lastEnd);
    }

    @Override
    public List<CarbonAccountingInitRespVO> getInitData(CarbonAccountingInitReqVO reqVO) {
        List<Long> userIds = reqVO.getCarbonUserInfoIds();
        LocalDate periodStart = reqVO.getAccountingPeriodStart();
        LocalDate periodEnd = reqVO.getAccountingPeriodEnd();

        // 1. 批量查询用户信息
        List<CarbonUserInfoDO> users = userInfoMapper.selectBatchIds(userIds);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        Map<Long, CarbonUserInfoDO> userMap = users.stream()
                .collect(Collectors.toMap(CarbonUserInfoDO::getId, u -> u));

        // 2. 初始化响应列表
        List<CarbonAccountingInitRespVO> resultList = new ArrayList<>();
        for (Long userId : userIds) {
            CarbonUserInfoDO user = userMap.get(userId);
            if (user == null) {
                continue;
            }
            CarbonAccountingInitRespVO resp = new CarbonAccountingInitRespVO();
            resp.setCarbonUserInfoId(user.getId());
            resp.setUsername(user.getUsername());
            resp.setIdCard(user.getIdCard());
            resp.setPhone(user.getPhone());
            resp.setAddress(user.getAddress());
            resp.setReformType(user.getReformType());
            resp.setDataSource(user.getDataSource());
            resp.setHeatingArea(user.getHeatingArea());
            resp.setProvinceCode(user.getProvinceCode());
            resp.setCityCode(user.getCityCode());
            resp.setDistrictCode(user.getDistrictCode());
            resultList.add(resp);
        }

        // 3. 批量填充分区划名称
        carbonUserInfoHelper.fillDivision(
                resultList,
                CarbonAccountingInitRespVO::getProvinceCode,
                CarbonAccountingInitRespVO::getCityCode,
                CarbonAccountingInitRespVO::getDistrictCode,
                CarbonAccountingInitRespVO::setDivision);

        // 4. 批量查询区域基准碳排放强度（按行政区编码分组查询）
        Map<String, BigDecimal> baselineMap = batchQueryBaselineIntensity(resultList);
        for (CarbonAccountingInitRespVO resp : resultList) {
            String areaKey = buildAreaKey(resp.getProvinceCode(), resp.getCityCode(), resp.getDistrictCode());
            BigDecimal baselineIntensity = baselineMap.getOrDefault(areaKey, BigDecimal.ZERO);
            resp.setBaselineIntensity(baselineIntensity);

            BigDecimal heatingArea = resp.getHeatingArea() != null ? resp.getHeatingArea() : BigDecimal.ZERO;
            BigDecimal baselineEmission = heatingArea.multiply(baselineIntensity).setScale(3, RoundingMode.HALF_UP);
            resp.setBaselineEmission(baselineEmission);
        }

        // 5. 批量计算采暖季用电量/用气量
        List<UsageContext> usageContexts = new ArrayList<>();
        Map<Long, BigDecimal[]> usageRefMap = new HashMap<>();

        for (CarbonUserInfoDO user : users) {
            BigDecimal[] refs = new BigDecimal[]{null, null}; // [0]=electricity, [1]=gas

            UsageContext ctx = new UsageContext();
            ctx.userId = user.getId();
            ctx.reformType = user.getReformType();
            ctx.dataSource = user.getDataSource();
            ctx.userCode = user.getUserCode();
            ctx.periodStart = periodStart;
            ctx.periodEnd = periodEnd;
            ctx.electricitySetter = val -> refs[0] = val;
            ctx.gasSetter = val -> refs[1] = val;
            usageContexts.add(ctx);
            usageRefMap.put(user.getId(), refs);
        }

        doBatchFillHeatingSeasonUsage(usageContexts);

        // 6. 设置用量并计算活动水平、排放因子、排放量
        Map<String, BigDecimal> factorMap = batchQueryEmissionFactors(resultList);

        for (CarbonAccountingInitRespVO resp : resultList) {
            BigDecimal[] refs = usageRefMap.get(resp.getCarbonUserInfoId());
            BigDecimal electricityUsage = refs != null ? refs[0] : null;
            BigDecimal gasUsage = refs != null ? refs[1] : null;
            resp.setElectricityUsage(electricityUsage);
            resp.setGasUsage(gasUsage);

            String reformType = resp.getReformType();
            String keyword = ReformTypeEnum.ELECTRIC_COAL.getType().equals(reformType) ? "电"
                    : ReformTypeEnum.GAS_COAL.getType().equals(reformType) ? "气" : "";

            BigDecimal activityLevel = ReformTypeEnum.ELECTRIC_COAL.getType().equals(reformType)
                    ? (electricityUsage != null ? electricityUsage : BigDecimal.ZERO)
                    : ReformTypeEnum.GAS_COAL.getType().equals(reformType)
                            ? (gasUsage != null ? gasUsage : BigDecimal.ZERO)
                            : BigDecimal.ZERO;
            resp.setActivityNameCode(reformType);
            resp.setActivityLevel(activityLevel);

            BigDecimal emissionFactor = factorMap.getOrDefault(keyword, BigDecimal.ZERO);
            resp.setEmissionFactor(emissionFactor);

            BigDecimal actualEmission = activityLevel.multiply(emissionFactor).setScale(3, RoundingMode.HALF_UP);
            resp.setActualEmission(actualEmission);
            resp.setReduction(resp.getBaselineEmission().subtract(actualEmission).setScale(3, RoundingMode.HALF_UP));
        }

        // 7. 批量查询设备列表（仅 dataSource=2 接口数据的用户需要查询碳设备）
        List<Long> dataSource2UserIds = resultList.stream()
                .filter(r -> "2".equals(r.getDataSource()))
                .map(CarbonAccountingInitRespVO::getCarbonUserInfoId)
                .collect(Collectors.toList());
        Map<Long, List<CarbonAccountingInitRespVO.DeviceItem>> deviceMap = batchQueryDevices(dataSource2UserIds);
        for (CarbonAccountingInitRespVO resp : resultList) {
            resp.setDeviceList(deviceMap.getOrDefault(resp.getCarbonUserInfoId(), Collections.emptyList()));
        }

        return resultList;
    }

    private Map<String, BigDecimal> batchQueryBaselineIntensity(List<CarbonAccountingInitRespVO> respList) {
        Set<String> areaKeys = respList.stream()
                .map(r -> buildAreaKey(r.getProvinceCode(), r.getCityCode(), r.getDistrictCode()))
                .collect(Collectors.toSet());

        Map<String, BigDecimal> result = new HashMap<>();
        for (String areaKey : areaKeys) {
            String[] parts = areaKey.split("-");
            Long provinceCode = parts[0].isEmpty() ? null : Long.parseLong(parts[0]);
            Long cityCode = parts[1].isEmpty() ? null : Long.parseLong(parts[1]);
            Long districtCode = parts[2].isEmpty() ? null : Long.parseLong(parts[2]);

            LambdaQueryWrapperX<CarbonBaselineDO> wrapper = new LambdaQueryWrapperX<>();
            if (provinceCode != null) wrapper.eq(CarbonBaselineDO::getProvinceCode, provinceCode);
            if (cityCode != null) wrapper.eq(CarbonBaselineDO::getCityCode, cityCode);
            if (districtCode != null) wrapper.eq(CarbonBaselineDO::getDistrictCode, districtCode);
            wrapper.last("LIMIT 1");

            CarbonBaselineDO baseline = baselineMapper.selectOne(wrapper);
            if (baseline != null && baseline.getIntensity() != null) {
                result.put(areaKey, baseline.getIntensity());
            }
        }
        return result;
    }

    private String buildAreaKey(Long provinceCode, Long cityCode, Long districtCode) {
        return (provinceCode != null ? provinceCode : "") + "-"
                + (cityCode != null ? cityCode : "") + "-"
                + (districtCode != null ? districtCode : "");
    }

    private Map<String, BigDecimal> batchQueryEmissionFactors(List<CarbonAccountingInitRespVO> respList) {
        Set<String> keywords = respList.stream()
                .map(r -> {
                    String reformType = r.getReformType();
                    return ReformTypeEnum.ELECTRIC_COAL.getType().equals(reformType) ? "电"
                            : ReformTypeEnum.GAS_COAL.getType().equals(reformType) ? "气" : "";
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

        Map<String, BigDecimal> result = new HashMap<>();
        for (String keyword : keywords) {
            CarbonFactorLibReqVO factorReq = new CarbonFactorLibReqVO();
            factorReq.setFactorName(keyword);
            List<CarbonFactorLibRespVO> factors = carbonFactorLibService.getFactorLibList(factorReq);
            if (CollUtil.isNotEmpty(factors) && factors.get(0).getFactorValue() != null) {
                result.put(keyword, factors.get(0).getFactorValue());
            }
        }
        return result;
    }

    private Map<Long, List<CarbonAccountingInitRespVO.DeviceItem>> batchQueryDevices(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        // 一次性批量查询所有用户的碳设备（isCarbon=1）
        CarbonDevicePageReqVO deviceReq = new CarbonDevicePageReqVO();
        deviceReq.setCarbonUserInfoIds(userIds);
        deviceReq.setIsCarbon(1);
        List<CarbonDeviceSimpleRespVO> allDevices = carbonDeviceService.getDeviceSimpleList(deviceReq);
        // 按 userId 分组
        return allDevices.stream().collect(Collectors.groupingBy(
                CarbonDeviceSimpleRespVO::getCarbonUserInfoId,
                Collectors.mapping(d -> {
                    CarbonAccountingInitRespVO.DeviceItem item = new CarbonAccountingInitRespVO.DeviceItem();
                    item.setId(d.getId());
                    item.setDeviceName(d.getDeviceName());
                    item.setDeviceCode(d.getDeviceCode());
                    return item;
                }, Collectors.toList())
        ));
    }

    /**
     * 根据核算周期开始日期推算所属采暖季年份（如 2025-11-15 → "2025"，2026-01-10 → "2025"）
     */
    private String determineHeatingSeason(LocalDate date) {
        int month = date.getMonthValue();
        int year = date.getYear();
        if (month >= 11) {
            return String.valueOf(year);
        } else if (month <= 3) {
            return String.valueOf(year - 1);
        }
        return String.valueOf(year);
    }

    /**
     * 采暖季用量计算上下文（内部桥接对象，统一两种 VO 的字段差异）
     */
    private static class UsageContext {
        Long userId;
        String reformType;
        String dataSource;
        String userCode;
        LocalDate periodStart;
        LocalDate periodEnd;
        java.util.function.Consumer<BigDecimal> electricitySetter;
        java.util.function.Consumer<BigDecimal> gasSetter;
    }

    @VisibleForTesting
    void validateAccountingExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonAccountingDO accounting = accountingMapper.selectById(id);
        if (accounting == null) {
            throw exception(ACCOUNTING_NOT_EXISTS);
        }
    }
}
