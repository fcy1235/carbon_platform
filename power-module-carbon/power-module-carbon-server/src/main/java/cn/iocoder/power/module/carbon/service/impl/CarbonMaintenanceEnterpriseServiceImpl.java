package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterprisePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonMaintenanceEnterpriseConvert;
import cn.iocoder.power.module.carbon.convert.CarbonMaintenanceStationConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasCoordinatorDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasSafetyOfficerDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceStationDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasCoordinatorMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasSafetyOfficerMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceEnterpriseMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceStationMapper;
import cn.iocoder.power.module.carbon.service.CarbonMaintenanceEnterpriseService;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonMaintenanceEnterpriseServiceImpl implements CarbonMaintenanceEnterpriseService {

    @Resource
    private CarbonMaintenanceEnterpriseMapper enterpriseMapper;

    @Resource
    private CarbonMaintenanceStationMapper stationMapper;

    @Resource
    private CarbonGasSafetyOfficerMapper officerMapper;

    @Resource
    private CarbonGasCoordinatorMapper coordinatorMapper;

    @Resource
    private AreaApi areaApi;

    @Override
    public Long createEnterprise(CarbonMaintenanceEnterpriseSaveReqVO createReqVO) {
        // 校验企业名称是否已存在
        validateEnterpriseNameUnique(createReqVO.getEnterpriseName(), null);
        // 校验统一社会信用代码是否已存在
        validateUnifiedSocialCreditCodeUnique(createReqVO.getUnifiedSocialCreditCode(), null);

        CarbonMaintenanceEnterpriseDO enterprise = CarbonMaintenanceEnterpriseConvert.INSTANCE.convert(createReqVO);
        enterpriseMapper.insert(enterprise);
        return enterprise.getId();
    }

    @Override
    public void updateEnterprise(CarbonMaintenanceEnterpriseSaveReqVO updateReqVO) {
        validateEnterpriseExists(updateReqVO.getId());
        // 校验企业名称是否已存在（排除自身）
        validateEnterpriseNameUnique(updateReqVO.getEnterpriseName(), updateReqVO.getId());
        // 校验统一社会信用代码是否已存在（排除自身）
        validateUnifiedSocialCreditCodeUnique(updateReqVO.getUnifiedSocialCreditCode(), updateReqVO.getId());

        CarbonMaintenanceEnterpriseDO updateObj = CarbonMaintenanceEnterpriseConvert.INSTANCE.convert(updateReqVO);
        enterpriseMapper.updateById(updateObj);
        // 省市区地址：updateById 跳过 null 字段，需要显式将被清空的下级地址置 null
        // 例如从"省-市-区"改为"省-市"时，countyCode 需要显式更新为 null
        if (updateReqVO.getCityCode() == null || updateReqVO.getCountyCode() == null) {
            LambdaUpdateWrapper<CarbonMaintenanceEnterpriseDO> addressWrapper = new LambdaUpdateWrapper<CarbonMaintenanceEnterpriseDO>()
                    .eq(CarbonMaintenanceEnterpriseDO::getId, updateReqVO.getId());
            if (updateReqVO.getCityCode() == null) {
                addressWrapper.set(CarbonMaintenanceEnterpriseDO::getCityCode, null);
                addressWrapper.set(CarbonMaintenanceEnterpriseDO::getCountyCode, null);
            } else if (updateReqVO.getCountyCode() == null) {
                addressWrapper.set(CarbonMaintenanceEnterpriseDO::getCountyCode, null);
            }
            enterpriseMapper.update(addressWrapper);
        }
    }

    @Override
    public void deleteEnterprise(Long id) {
        validateEnterpriseExists(id);
        // 校验是否存在关联网点
        List<CarbonMaintenanceStationDO> stations = stationMapper.selectByEnterpriseId(id);
        if (CollUtil.isNotEmpty(stations)) {
            throw exception(MAINTENANCE_ENTERPRISE_HAS_STATIONS);
        }
        // 校验是否存在关联安全员
        List<CarbonGasSafetyOfficerDO> officers = officerMapper.selectByEnterpriseId(id);
        if (CollUtil.isNotEmpty(officers)) {
            throw exception(MAINTENANCE_ENTERPRISE_HAS_OFFICERS);
        }
        // 校验是否存在关联协管员
        List<CarbonGasCoordinatorDO> coordinators = coordinatorMapper.selectByEnterpriseId(id);
        if (CollUtil.isNotEmpty(coordinators)) {
            throw exception(MAINTENANCE_ENTERPRISE_HAS_COORDINATORS);
        }
        enterpriseMapper.deleteById(id);
    }

    @Override
    public CarbonMaintenanceEnterpriseRespVO getEnterprise(Long id) {
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            return null;
        }
        CarbonMaintenanceEnterpriseRespVO respVO = CarbonMaintenanceEnterpriseConvert.INSTANCE.convert(enterprise);
        // 填充市、县名称
        fillAreaNames(respVO);
        // 动态计算汇总数据
        fillStationSummary(Collections.singletonList(respVO));


        return respVO;
    }

    @Override
    public PageResult<CarbonMaintenanceEnterpriseRespVO> getEnterprisePage(CarbonMaintenanceEnterprisePageReqVO pageReqVO) {
        PageResult<CarbonMaintenanceEnterpriseDO> pageResult = enterpriseMapper.selectPage(pageReqVO);
        PageResult<CarbonMaintenanceEnterpriseRespVO> resultVo = CarbonMaintenanceEnterpriseConvert.INSTANCE.convertPage(pageResult);
        if (CollUtil.isNotEmpty(resultVo.getList())) {
            fillAreaNames(resultVo.getList());
            // 动态计算汇总数据
            fillStationSummary(resultVo.getList());
        }
        return resultVo;
    }

    @Override
    public List<CarbonMaintenanceEnterpriseRespVO> getEnterpriseList(CarbonMaintenanceEnterprisePageReqVO pageReqVO) {
        List<CarbonMaintenanceEnterpriseDO> list = enterpriseMapper.selectList(pageReqVO);
        List<CarbonMaintenanceEnterpriseRespVO> result = CarbonMaintenanceEnterpriseConvert.INSTANCE.convertList(list);
        if (CollUtil.isNotEmpty(result)) {
            fillAreaNames(result);
            fillStationSummary(result);
        }
        return result;
    }

    @VisibleForTesting
    void validateEnterpriseExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectById(id);
        if (enterprise == null) {
            throw exception(MAINTENANCE_ENTERPRISE_NOT_EXISTS);
        }
    }

    private void validateEnterpriseNameUnique(String enterpriseName, Long id) {
        if (enterpriseName == null) {
            return;
        }
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectByEnterpriseName(enterpriseName);
        if (enterprise == null) {
            return;
        }
        // 如果 id 为空，说明是新增；如果 id 不为空，说明是更新，需要排除自身
        if (id == null || !enterprise.getId().equals(id)) {
            throw exception(MAINTENANCE_ENTERPRISE_NAME_EXISTS);
        }
    }

    private void validateUnifiedSocialCreditCodeUnique(String unifiedSocialCreditCode, Long id) {
        if (unifiedSocialCreditCode == null) {
            return;
        }
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectByUnifiedSocialCreditCode(unifiedSocialCreditCode);
        if (enterprise == null) {
            return;
        }
        // 如果 id 为空，说明是新增；如果 id 不为空，说明是更新，需要排除自身
        if (id == null || !enterprise.getId().equals(id)) {
            throw exception(MAINTENANCE_ENTERPRISE_CREDIT_CODE_EXISTS);
        }
    }

    /**
     * 批量填充企业的网点汇总数据（维保人员总数、覆盖村数、覆盖户数、网点数量）
     */
    private void fillStationSummary(List<CarbonMaintenanceEnterpriseRespVO> list) {
        Set<Long> enterpriseIds = CollectionUtils.convertSet(list, CarbonMaintenanceEnterpriseRespVO::getId);

        if (enterpriseIds.isEmpty()) {
            return;
        }
        // 批量查询所有关联网点
        List<CarbonMaintenanceStationDO> allStations = stationMapper.selectByEnterpriseIds(enterpriseIds);
        // 按企业ID分组
        Map<Long, List<CarbonMaintenanceStationDO>> stationMap = allStations.stream().collect(Collectors.groupingBy(CarbonMaintenanceStationDO::getEnterpriseId));
        // 计算汇总并填充
        list.forEach(item -> {
            List<CarbonMaintenanceStationDO> stations = stationMap.getOrDefault(item.getId(), Collections.emptyList());
            item.setStationCount(stations.size());
            item.setMaintenanceStaffCount(stations.stream()
                    .mapToInt(s -> s.getMaintenanceStaffCount() != null ? s.getMaintenanceStaffCount() : 0)
                    .sum());
            item.setCoveredVillages(stations.stream()
                    .mapToInt(s -> s.getCoveredVillages() != null ? s.getCoveredVillages() : 0)
                    .sum());
            item.setCoveredHouseholds(stations.stream()
                    .mapToInt(s -> s.getCoveredHouseholds() != null ? s.getCoveredHouseholds() : 0)
                    .sum());
            item.setStationList(CarbonMaintenanceStationConvert.INSTANCE.convertList(stations));
        });
    }

    private void fillAreaNames(CarbonMaintenanceEnterpriseRespVO respVO) {
        Set<Long> areaIds = new HashSet<>();
        if (respVO.getProvinceCode() != null) {
            areaIds.add(respVO.getProvinceCode());
        }
        if (respVO.getCityCode() != null) {
            areaIds.add(respVO.getCityCode());
        }
        if (respVO.getCountyCode() != null) {
            areaIds.add(respVO.getCountyCode());
        }
        if (areaIds.isEmpty()) {
            return;
        }
        Map<Long, String> areaMap = getAreaNameMap(areaIds);
        respVO.setProvinceName(areaMap.get(respVO.getProvinceCode()));
        respVO.setCityName(areaMap.get(respVO.getCityCode()));
        respVO.setCountyName(areaMap.get(respVO.getCountyCode()));
    }

    private void fillAreaNames(List<CarbonMaintenanceEnterpriseRespVO> list) {
        Set<Long> areaIds = new HashSet<>();
        list.forEach(item -> {
            if (item.getProvinceCode() != null) {
                areaIds.add(item.getProvinceCode());
            }
            if (item.getCityCode() != null) {
                areaIds.add(item.getCityCode());
            }
            if (item.getCountyCode() != null) {
                areaIds.add(item.getCountyCode());
            }
        });
        if (areaIds.isEmpty()) {
            return;
        }
        Map<Long, String> areaMap = getAreaNameMap(areaIds);
        list.forEach(item -> {
            item.setProvinceName(areaMap.get(item.getProvinceCode()));
            item.setCityName(areaMap.get(item.getCityCode()));
            item.setCountyName(areaMap.get(item.getCountyCode()));
        });
    }

    private Map<Long, String> getAreaNameMap(Set<Long> areaIds) {
        CommonResult<List<AreaRespDTO>> result = areaApi.getAreaList(areaIds);
        List<AreaRespDTO> areaList = result.getData();
        return CollectionUtils.convertMap(areaList, AreaRespDTO::getId, AreaRespDTO::getName);
    }
}
