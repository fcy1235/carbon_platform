package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.CarbonAccountingPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselinePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineRespVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.*;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.*;
import cn.iocoder.power.module.carbon.dal.mysql.*;
import cn.iocoder.power.module.carbon.service.CarbonBaselineService;
import cn.iocoder.power.module.carbon.service.CarbonReportService;
import cn.iocoder.power.module.carbon.util.AreaUtils;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.PROJECT_NOT_EXISTS;
import static com.alibaba.fastjson.JSONPatch.OperationType.add;

@Service
@Validated
@Slf4j
public class CarbonReportServiceImpl implements CarbonReportService {

    @Resource
    private CarbonReportMapper reportMapper;

    @Resource
    private AreaUtils areaUtils;

    @Resource
    private AreaApi areaApi;

    // ==================== 仪表盘统计相关 Mapper ====================

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Resource
    private CarbonBaselineMapper baselineMapper;

    @Resource
    private CarbonAccountingMapper accountingMapper;

    @Resource
    private CarbonGasSafetyOfficerMapper safetyOfficerMapper;

    @Resource
    private CarbonGasCoordinatorMapper coordinatorMapper;

    @Resource
    private CarbonMaintenanceEnterpriseMapper enterpriseMapper;

    @Resource
    private CarbonMaintenanceStationMapper stationMapper;

    @Resource
    private CarbonDeviceMapper deviceMapper;

    // ==================== 项目碳减排报表相关 Mapper ====================

    @Resource
    private CarbonProjectMapper projectMapper;

    @Resource
    private CarbonProjectUserMapper projectUserMapper;

    @Resource
    private CarbonAccountingActivityMapper activityMapper;

    @Resource
    private CarbonContactMapper contactMapper;

    @Resource
    private CarbonBaselineService carbonBaselineService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<CarbonGasUsageReportRespVO> getGasUsageReport(CarbonGasUsageReportReqVO reqVO) {
        List<CarbonGasUsageReportRespVO> list = reportMapper.selectGasUsageReport(
                reqVO.getStartTime(), reqVO.getEndTime(), reqVO.getKeyword(), reqVO.getGasId());
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        for (CarbonGasUsageReportRespVO vo : list) {
            formatGasReport(vo);
        }
        return list;
    }

    @Override
    public List<CarbonElectricityUsageReportRespVO> getElectricityUsageReport(CarbonElectricityUsageReportReqVO reqVO) {
        List<CarbonElectricityUsageReportRespVO> list = reportMapper.selectElectricityUsageReport(
                reqVO.getStartTime(), reqVO.getEndTime(), reqVO.getKeyword(), reqVO.getElectricityId());
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        for (CarbonElectricityUsageReportRespVO vo : list) {
            formatElectricityReport(vo);
        }
        return list;
    }

    @Override
    public List<CarbonReformAccountReportRespVO> getReformAccountReport(CarbonReformAccountReportReqVO reqVO) {
        List<CarbonReformAccountReportRespVO> list = reportMapper.selectReformAccountReport(
                reqVO.getProvinceCode(), reqVO.getCityCode(), reqVO.getDistrictCode(),
                reqVO.getTownCode(), reqVO.getVillageCode(),
                reqVO.getKeyword(), reqVO.getPhone(), reqVO.getReformType(),
                reqVO.getUseStatus(), reqVO.getReformYear());
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        // 填充行政区划名称
        areaUtils.fillReformAreaNames(list);
        return list;
    }

    private void formatGasReport(CarbonGasUsageReportRespVO vo) {
        if (vo.getStartTime() != null && vo.getStartTotal() != null) {
            vo.setStartReading(vo.getStartTime().format(DATE_FORMATTER) + "（" + vo.getStartTotal() + "）");
        } else {
            vo.setStartReading("");
        }
        if (vo.getEndTime() != null && vo.getEndTotal() != null) {
            vo.setEndReading(vo.getEndTime().format(DATE_FORMATTER) + "（" + vo.getEndTotal() + "）");
        } else {
            vo.setEndReading("");
        }
        if (vo.getEndTotal() != null && vo.getStartTotal() != null) {
            vo.setTotalUsage(vo.getEndTotal().subtract(vo.getStartTotal()));
        } else {
            vo.setTotalUsage(BigDecimal.ZERO);
        }
        // 清空内部字段，避免序列化
        vo.setStartTime(null);
        vo.setStartTotal(null);
        vo.setEndTime(null);
        vo.setEndTotal(null);
    }

    private void formatElectricityReport(CarbonElectricityUsageReportRespVO vo) {
        if (vo.getStartTime() != null && vo.getStartTotal() != null) {
            vo.setStartReading(vo.getStartTime().format(DATE_FORMATTER) + "（" + vo.getStartTotal() + "）");
        } else {
            vo.setStartReading("");
        }
        if (vo.getEndTime() != null && vo.getEndTotal() != null) {
            vo.setEndReading(vo.getEndTime().format(DATE_FORMATTER) + "（" + vo.getEndTotal() + "）");
        } else {
            vo.setEndReading("");
        }
        if (vo.getEndTotal() != null && vo.getStartTotal() != null) {
            vo.setTotalUsage(vo.getEndTotal().subtract(vo.getStartTotal()));
        } else {
            vo.setTotalUsage(BigDecimal.ZERO);
        }
        // 清空内部字段，避免序列化
        vo.setStartTime(null);
        vo.setStartTotal(null);
        vo.setEndTime(null);
        vo.setEndTotal(null);
    }

    // ==================== 仪表盘统计 ====================

    @Override
    public CarbonDashboardRespVO getDashboard() {
        CarbonDashboardRespVO.CarbonDashboardRespVOBuilder builder = CarbonDashboardRespVO.builder();

        // ========== 批量查询数据，同表只查一次 ==========

        // carbon_user_info：一次查询 id、区县编码、改造类型
        List<CarbonUserInfoDO> allUsers = userInfoMapper.selectList();

        // carbon_accounting：一次查询关联用户ID、实际排放量、减排量
        List<CarbonAccountingDO> allAccountings = accountingMapper.selectList(new LambdaQueryWrapperX<CarbonAccountingDO>()
                .select(CarbonAccountingDO::getCarbonUserInfoId, CarbonAccountingDO::getActualEmission,
                        CarbonAccountingDO::getReduction));

        // carbon_baseline：查询基准线排放强度
        List<CarbonBaselineDO> allBaselines = baselineMapper.selectList();

        // carbon_maintenance_station：一次查询覆盖村数
        List<CarbonMaintenanceStationDO> allStations = stationMapper.selectList(
                new LambdaQueryWrapperX<CarbonMaintenanceStationDO>()
                        .select(CarbonMaintenanceStationDO::getCoveredVillages));

        // 以下三张表各只查一次，无法合并
        long safetyCount = safetyOfficerMapper.selectCount(new LambdaQueryWrapperX<CarbonGasSafetyOfficerDO>()
                .eq(CarbonGasSafetyOfficerDO::getStaffStatus, "1"));
        long coordinatorCount = coordinatorMapper.selectCount(new LambdaQueryWrapperX<CarbonGasCoordinatorDO>()
                .eq(CarbonGasCoordinatorDO::getStaffStatus, "1"));
        long enterpriseCount = enterpriseMapper.selectCount();
        List<CarbonDeviceDO> allDevices = deviceMapper.selectList(new LambdaQueryWrapperX<CarbonDeviceDO>()
                .select(CarbonDeviceDO::getStatus));

        // ========== 从批量数据中计算各统计项 ==========

        // 1. 取暖改造户数统计（从 allUsers 按 reformType 分组计数）
        long totalCount = allUsers.size();
        Map<String, Long> reformTypeCountMap = allUsers.stream()
                .collect(Collectors.groupingBy(u -> u.getReformType() != null ? u.getReformType() : "",
                        Collectors.counting()));
        long gasCoalCount = reformTypeCountMap.getOrDefault("2", 0L);
        long electricCoalCount = reformTypeCountMap.getOrDefault("1", 0L);
        builder.reformHouseholdStats(CarbonDashboardRespVO.ReformHouseholdStats.builder()
                .gasCoalCount(gasCoalCount).electricCoalCount(electricCoalCount).totalCount(totalCount).build());

        // 2. 减碳排量（从 allBaselines + allAccountings 汇总）
//        BigDecimal baselineEmission = allBaselines.stream()
//                .map(CarbonBaselineDO::getIntensity).filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 基准线排放量 = 基准线排放强度 * 面积
        BigDecimal baselineEmission = calcTotalBaselineEmission(allUsers, allBaselines, allAccountings);

        BigDecimal actualEmission = allAccountings.stream()
                .map(CarbonAccountingDO::getActualEmission).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReduction = allAccountings.stream()
                .map(CarbonAccountingDO::getReduction).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        builder.carbonReductionStats(CarbonDashboardRespVO.CarbonReductionStats.builder()
                .baselineEmission(baselineEmission).actualEmission(actualEmission)
                .totalReduction(totalReduction).build());

        // 3. 两员人数
        builder.staffStats(CarbonDashboardRespVO.StaffStats.builder()
                .safetyOfficerCount(safetyCount).coordinatorCount(coordinatorCount)
                .totalCount(safetyCount + coordinatorCount).build());

        // 4. 维保统计（从 allStations 取网点数和覆盖村数）
        long stationCount = allStations.size();
        long coveredVillageCount = allStations.stream()
                .mapToLong(s -> s.getCoveredVillages() != null ? s.getCoveredVillages() : 0).sum();
        builder.maintenanceStats(CarbonDashboardRespVO.MaintenanceStats.builder()
                .enterpriseCount(enterpriseCount).stationCount(stationCount)
                .coveredVillageCount(coveredVillageCount).build());

        // 5. 改造类别统计（复用已计算的户数数据）
        List<CarbonDashboardRespVO.ReformCategoryItem> categoryList = new ArrayList<>();
        categoryList.add(CarbonDashboardRespVO.ReformCategoryItem.builder()
                .name("煤改气").count(gasCoalCount)
                .percentage(calcPercentage(gasCoalCount, totalCount)).build());
        categoryList.add(CarbonDashboardRespVO.ReformCategoryItem.builder()
                .name("煤改电").count(electricCoalCount)
                .percentage(calcPercentage(electricCoalCount, totalCount)).build());
        builder.reformCategoryStats(categoryList);

        // ========== 6/7/9 三项都需要区县名称解析，统一收集编码一次调用 ==========

        // 6. 各区县碳排放量（从 allUsers + allAccountings 按区县汇总）
        Map<Long, BigDecimal> accountingByUser = allAccountings.stream()
                .collect(Collectors.groupingBy(CarbonAccountingDO::getCarbonUserInfoId,
                        Collectors.reducing(BigDecimal.ZERO,
                                a -> a.getActualEmission() != null ? a.getActualEmission() : BigDecimal.ZERO,
                                BigDecimal::add)));
        Map<Long, BigDecimal> districtEmissionMap = new TreeMap<>(Comparator.reverseOrder());
        for (CarbonUserInfoDO user : allUsers) {
            if (user.getDistrictCode() == null) {
                continue;
            }
            BigDecimal emission = accountingByUser.getOrDefault(user.getId(), BigDecimal.ZERO);
            districtEmissionMap.merge(user.getDistrictCode(), emission, BigDecimal::add);
        }

        // 7. 区域分布（从 allUsers 按市分组计数）
        Map<Long, Long> regionDistMap = allUsers.stream()
                .filter(u -> u.getCityCode() != null)
                .collect(Collectors.groupingBy(CarbonUserInfoDO::getCityCode, Collectors.counting()));

        // 9. 各区县双代实施情况（从 allUsers 按区县+改造类型分组）
        Map<Long, Map<String, Long>> districtReformMap = allUsers.stream()
                .filter(u -> u.getDistrictCode() != null)
                .collect(Collectors.groupingBy(CarbonUserInfoDO::getDistrictCode,
                        Collectors.groupingBy(u -> u.getReformType() != null ? u.getReformType() : "",
                                Collectors.counting())));

        // 统一收集所有区县编码，一次调用 resolveAreaNames（替代原来 3 次 areaApi 调用）
        Set<Long> allDistrictCodes = new HashSet<>();
        allDistrictCodes.addAll(districtEmissionMap.keySet());
        allDistrictCodes.addAll(regionDistMap.keySet());
        allDistrictCodes.addAll(districtReformMap.keySet());
        Map<Long, String> areaNameMap = resolveAreaNames(allDistrictCodes);

        // 组装各区县碳排放量
        if (!districtEmissionMap.isEmpty()) {
            builder.districtCarbonEmissions(districtEmissionMap.entrySet().stream()
                    .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                    .map(e -> CarbonDashboardRespVO.DistrictCarbonEmission.builder()
                            .districtCode(e.getKey())
                            .districtName(areaNameMap.getOrDefault(e.getKey(), ""))
                            .carbonEmission(e.getValue())
                            .build())
                    .collect(Collectors.toList()));
        } else {
            builder.districtCarbonEmissions(Collections.emptyList());
        }

        // 组装区域分布
        if (!regionDistMap.isEmpty()) {
            builder.regionDistribution(regionDistMap.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .map(e -> CarbonDashboardRespVO.RegionDistributionItem.builder()
                            .regionCode(e.getKey())
                            .regionName(areaNameMap.getOrDefault(e.getKey(), ""))
                            .value(e.getValue())
                            .build())
                    .collect(Collectors.toList()));
        } else {
            builder.regionDistribution(Collections.emptyList());
        }

        // 8. 设备状态
        if (!CollUtil.isEmpty(allDevices)) {
            Map<String, Long> statusCountMap = allDevices.stream()
                    .collect(Collectors.groupingBy(CarbonDeviceDO::getStatus, Collectors.counting()));
            long deviceTotal = allDevices.size();
            builder.deviceStatusStats(statusCountMap.entrySet().stream().map(e -> {
                String status = e.getKey();
                long count = e.getValue();
                String name;
                switch (status) {
                    case "1":
                        name = "正常";
                        break;
                    case "2":
                        name = "停运";
                        break;
                    case "3":
                        name = "故障";
                        break;
                    default:
                        name = "未知";
                }
                return CarbonDashboardRespVO.DeviceStatusItem.builder()
                        .name(name).status(status).count(count)
                        .percentage(calcPercentage(count, deviceTotal))
                        .build();
            }).collect(Collectors.toList()));
        } else {
            builder.deviceStatusStats(Collections.emptyList());
        }

        // 组装各区县双代实施情况
        if (!districtReformMap.isEmpty()) {
            builder.districtReformStats(districtReformMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> {
                        Map<String, Long> typeMap = e.getValue();
                        return CarbonDashboardRespVO.DistrictReformStats.builder()
                                .districtCode(e.getKey())
                                .districtName(areaNameMap.getOrDefault(e.getKey(), ""))
                                .gasCoalCount(typeMap.getOrDefault("2", 0L))
                                .electricCoalCount(typeMap.getOrDefault("1", 0L))
                                .build();
                    })
                    .collect(Collectors.toList()));
        } else {
            builder.districtReformStats(Collections.emptyList());
        }

        return builder.build();
    }

    // ==================== 工具方法 ====================

    private Map<Long, String> resolveAreaNames(Set<Long> areaIds) {
        // 创建一个副本以避免修改原始Set（特别是当areaIds是TreeMap的keySet时，直接remove(null)会抛出NullPointerException）
        Set<Long> filteredIds = new HashSet<>(areaIds);
        filteredIds.remove(null);
        filteredIds.remove(0L);
        if (CollUtil.isEmpty(filteredIds)) {
            return Collections.emptyMap();
        }
        CommonResult<List<AreaRespDTO>> result = areaApi.getAreaList(filteredIds);
        if (result == null || result.getData() == null) {
            return Collections.emptyMap();
        }
        return CollectionUtils.convertMap(result.getData(), AreaRespDTO::getId, AreaRespDTO::getName);
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calcPercentage(long count, long total) {
        if (total == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(count)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算全部台账总的基准线排放量
     *
     * @param allUsers       用户信息集合
     * @param allBaselines   基准线集合
     * @param allAccountings 碳核算台账集合
     * @return 总基准排放量
     */
    public static BigDecimal calcTotalBaselineEmission(
            List<CarbonUserInfoDO> allUsers,
            List<CarbonBaselineDO> allBaselines,
            List<CarbonAccountingDO> allAccountings
    ) {
        // 1. 用户ID -> 用户信息 Map，快速根据id查询用户
        Map<Long, CarbonUserInfoDO> userMap = allUsers.stream().collect(Collectors.toMap(CarbonUserInfoDO::getId, user -> user));

        // 2. 构建基准线分组key：省编码_市编码_区编码_改造类型 -> 基准线对象
        Map<String, CarbonBaselineDO> baselineMap = allBaselines.stream()
                .collect(Collectors.toMap(
                        baseline -> buildBaselineKey(baseline),
                        baseline -> baseline));

        // 3. 遍历每一条核算台账，累加基准排放量
        BigDecimal totalBaselineEmission = BigDecimal.ZERO;
        for (CarbonAccountingDO accounting : allAccountings) {
            Long userInfoId = accounting.getCarbonUserInfoId();
            // 不存在对应用户数据则跳过
            CarbonUserInfoDO userInfo = userMap.get(userInfoId);
            if (userInfo == null || userInfo.getHeatingArea() == null) {
                continue;
            }
            // 组装匹配key，查询对应基准线
            String matchKey = buildUserBaselineKey(userInfo);
            CarbonBaselineDO baseline = baselineMap.get(matchKey);
            log.info("面积：{},matchKey：{}", userInfo.getHeatingArea(), matchKey);

            if (ObjectUtil.isEmpty(baseline) || baseline.getIntensity() == null) {
                continue;
            }
            // 单条基准排放量 = 排放强度 * 面积
            BigDecimal singleEmission = baseline.getIntensity()
                    .multiply(userInfo.getHeatingArea());
            totalBaselineEmission = totalBaselineEmission.add(singleEmission);
            log.info("单条基准排放量：{}，累计基准排放量：{}", singleEmission, totalBaselineEmission);
        }

        // 保留4位小数，业务可自行调整
        return totalBaselineEmission.setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 组装基准线唯一key：provinceCode_cityCode_districtCode_reformType
     */
    private static String buildBaselineKey(CarbonBaselineDO baseline) {
        return String.format("%s_%s_%s",
                baseline.getProvinceCode(),
                baseline.getCityCode(),
                baseline.getDistrictCode()
        );
    }

    /**
     * 根据用户信息组装匹配基准线的key
     */
    private static String buildUserBaselineKey(CarbonUserInfoDO user) {
        return String.format("%s_%s_%s",
                user.getProvinceCode(),
                user.getCityCode(),
                user.getDistrictCode()
        );
    }

    // ==================== 项目碳减排报表 ====================

    @Override
    public CarbonProjectReportRespVO getProjectReport(Long projectId) {
        // 1. 查询项目基本信息
        CarbonProjectDO project = projectMapper.selectById(projectId);
        if (project == null) {
            throw exception(PROJECT_NOT_EXISTS);
        }

        CarbonProjectReportRespVO respVO = new CarbonProjectReportRespVO();
        respVO.setProjectId(project.getId());
        respVO.setProjectName(project.getProjectName());
        respVO.setPlanStartDate(project.getPlanStartDate());
        respVO.setPlanEndDate(project.getPlanEndDate());
        respVO.setReduction(project.getTotalReduction());
        respVO.setProjectDesc(project.getProjectDesc());

        // 查询 项目负责人
        CarbonContactDO contact = contactMapper.selectById(project.getContactId());
        if (contact != null) {
            respVO.setContactName(contact.getName());
        }


        // 2. 行政区中文名称
        respVO.setDivision(buildDivision(project.getProvinceCode(), project.getCityCode(), project.getDistrictCode()));

        // 3. 区域基准线碳排放强度（根据项目行政区匹配）
        if (project.getProvinceCode() != null && project.getCityCode() != null && project.getDistrictCode() != null) {
            CarbonBaselinePageReqVO baselineReq = new CarbonBaselinePageReqVO();
            baselineReq.setProvinceCode(project.getProvinceCode());
            baselineReq.setCityCode(project.getCityCode());
            baselineReq.setDistrictCode(project.getDistrictCode());
            List<CarbonBaselineRespVO> baselineList = carbonBaselineService.getBaselineList(baselineReq);
            if (CollUtil.isNotEmpty(baselineList)) {
                respVO.setBaselineIntensity(baselineList.get(0).getIntensity());
            }
        }

        // 4. 项目关联用户：改造户数、建造面积
        List<CarbonProjectUserDO> projectUsers = projectUserMapper.selectListByProjectId(projectId);
        respVO.setReformHouseholds(projectUsers.size());
        respVO.setBuildingArea(BigDecimal.ZERO);
        Set<Long> userInfoIds = CollectionUtils.convertSet(projectUsers, CarbonProjectUserDO::getCarbonUserInfoId);

        if (CollUtil.isNotEmpty(userInfoIds)) {
            List<CarbonUserInfoDO> userInfos = userInfoMapper.selectByIds(userInfoIds);
            BigDecimal buildingArea = CollectionUtils.getSumValue(userInfos, CarbonUserInfoDO::getHeatingArea, BigDecimal::add);
            respVO.setBuildingArea(buildingArea);
        }

        // 5、区域基准线碳排放量
        BigDecimal baselineEmission = respVO.getBuildingArea().multiply(respVO.getBaselineIntensity());
        if (ObjectUtil.isNotEmpty(baselineEmission)) {
            respVO.setBaselineEmission(baselineEmission.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
        }


        // 5. 联查碳核算 carbon_accounting + carbon_accounting_activity
        //    按项目关联用户 + 项目时间区间过滤
        //    - actual_emission / reduction 累加 = 总实际排放量 / 总减排量
        //    - activity_name_code = "1" 电力，"2" 燃气
        //    - 电力/燃气排放因子各取第一条 activity 的 emission_factor
        //    - 电力/燃气消耗量 = 对应活动的 activity_level 累加
        respVO.setActualEmission(BigDecimal.ZERO);
        respVO.setReduction(BigDecimal.ZERO);
        respVO.setElectricityUsage(BigDecimal.ZERO);
        respVO.setGasUsage(BigDecimal.ZERO);

        if (CollUtil.isNotEmpty(userInfoIds)) {
            // 5.1 按用户 + 项目时间区间查询碳核算记录
            CarbonAccountingPageReqVO pageReqVO = new CarbonAccountingPageReqVO();
            pageReqVO.setCarbonUserInfoIds(userInfoIds);
            pageReqVO.setAccountingPeriodStart(project.getPlanStartDate());
            pageReqVO.setAccountingPeriodEnd(project.getPlanEndDate());

            List<CarbonAccountingWithUserDTO> accountingList = accountingMapper.selectListWithUser(pageReqVO);


            if (!accountingList.isEmpty()) {
                // 实际排放量
                BigDecimal totalActualEmission = CollectionUtils.getSumValue(accountingList, CarbonAccountingWithUserDTO::getActualEmission, BigDecimal::add);
                // 实际减排量
                BigDecimal totalReduction = CollectionUtils.getSumValue(accountingList, CarbonAccountingWithUserDTO::getReduction, BigDecimal::add);
                if (ObjectUtil.isNotEmpty(totalActualEmission)) {
                    respVO.setActualEmission(totalActualEmission.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
                }
                if (ObjectUtil.isNotEmpty(totalReduction)){
                    respVO.setReduction(totalReduction.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
                }

                // 电力消耗量
                List<CarbonAccountingWithUserDTO> electricityList = CollectionUtils.filterList(accountingList, accounting -> "1".equals(accounting.getActivityNameCode()));
                BigDecimal totalElectricityUsage = CollectionUtils.getSumValue(electricityList, CarbonAccountingWithUserDTO::getActualEmission, BigDecimal::add);
                // 燃气消耗量
                List<CarbonAccountingWithUserDTO> gasList = CollectionUtils.filterList(accountingList, accounting -> "2".equals(accounting.getActivityNameCode()));
                BigDecimal totalGasUsage = CollectionUtils.getSumValue(gasList, CarbonAccountingWithUserDTO::getActualEmission, BigDecimal::add);

                if (ObjectUtil.isNotEmpty(totalElectricityUsage)){
                    respVO.setElectricityUsage(totalElectricityUsage.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
                }
                if (ObjectUtil.isNotEmpty(totalGasUsage)){
                    respVO.setGasUsage(totalGasUsage.divide(BigDecimal.valueOf(10000), 4, RoundingMode.HALF_UP));
                }
                // 电力/燃气排放因子
                if (!electricityList.isEmpty()) {
                    log.info("电气因子");
                    respVO.setElectricityFactor(CollectionUtils.getFirst(electricityList).getEmissionFactor());
                }
                if (!gasList.isEmpty()) {
                    log.info("燃气因子：{},{}", CollectionUtils.getFirst(gasList).getEmissionFactor(), CollectionUtils.getFirst(gasList));
                    respVO.setGasFactor(CollectionUtils.getFirst(gasList).getEmissionFactor());
                }

            }
        }

        return respVO;
    }

    /**
     * 根据省市区编码拼接行政区中文名称
     */
    private String buildDivision(Long provinceCode, Long cityCode, Long districtCode) {
        Set<Long> areaIds = new HashSet<>();
        if (provinceCode != null) areaIds.add(provinceCode);
        if (cityCode != null) areaIds.add(cityCode);
        if (districtCode != null) areaIds.add(districtCode);
        if (areaIds.isEmpty()) {
            return "";
        }
        CommonResult<List<AreaRespDTO>> result = areaApi.getAreaList(areaIds);
        if (result == null || result.getData() == null) {
            return "";
        }
        Map<Long, String> areaMap = CollectionUtils.convertMap(result.getData(), AreaRespDTO::getId, AreaRespDTO::getName);
        return StringUtils.join(areaMap.get(provinceCode), areaMap.get(cityCode), areaMap.get(districtCode));
    }
}
