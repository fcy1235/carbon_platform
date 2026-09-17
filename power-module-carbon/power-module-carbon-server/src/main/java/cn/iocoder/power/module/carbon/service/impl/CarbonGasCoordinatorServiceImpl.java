package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonGasCoordinatorConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasCoordinatorDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasCoordinatorMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceEnterpriseMapper;
import cn.iocoder.power.module.carbon.service.CarbonGasCoordinatorService;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonGasCoordinatorServiceImpl implements CarbonGasCoordinatorService {

    @Resource
    private CarbonGasCoordinatorMapper coordinatorMapper;

    @Resource
    private CarbonMaintenanceEnterpriseMapper enterpriseMapper;

    @Resource
    private AreaApi areaApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoordinator(CarbonGasCoordinatorSaveReqVO createReqVO) {
        CarbonGasCoordinatorDO coordinator = CarbonGasCoordinatorConvert.INSTANCE.convert(createReqVO);
        coordinatorMapper.insert(coordinator);
        return coordinator.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoordinator(CarbonGasCoordinatorSaveReqVO updateReqVO) {
        validateCoordinatorExists(updateReqVO.getId());
        CarbonGasCoordinatorDO updateObj = CarbonGasCoordinatorConvert.INSTANCE.convert(updateReqVO);
        coordinatorMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoordinator(Long id) {
        CarbonGasCoordinatorDO coordinator = coordinatorMapper.selectById(id);
        if (coordinator == null) {
            throw exception(GAS_COORDINATOR_NOT_EXISTS);
        }
        coordinatorMapper.deleteById(id);
    }

    @Override
    public CarbonGasCoordinatorRespVO getCoordinator(Long id) {
        CarbonGasCoordinatorDO coordinator = coordinatorMapper.selectById(id);
        if (coordinator == null) {
            return null;
        }
        CarbonGasCoordinatorRespVO respVO = CarbonGasCoordinatorConvert.INSTANCE.convert(coordinator);
        fillAreaNames(Collections.singletonList(respVO));
        fillEnterpriseName(respVO);
        return respVO;
    }

    @Override
    public PageResult<CarbonGasCoordinatorRespVO> getCoordinatorPage(CarbonGasCoordinatorPageReqVO pageReqVO) {
        PageResult<CarbonGasCoordinatorDO> pageResult = coordinatorMapper.selectPage(pageReqVO);
        PageResult<CarbonGasCoordinatorRespVO> resultVo = CarbonGasCoordinatorConvert.INSTANCE.convertPage(pageResult);
        if (CollUtil.isNotEmpty(resultVo.getList())) {
            fillAreaNames(resultVo.getList());
            fillEnterpriseNames(resultVo.getList());
        }
        return resultVo;
    }

    @Override
    public List<CarbonGasCoordinatorRespVO> getCoordinatorList(CarbonGasCoordinatorPageReqVO pageReqVO) {
        List<CarbonGasCoordinatorDO> list = coordinatorMapper.selectList(pageReqVO);
        List<CarbonGasCoordinatorRespVO> result = CarbonGasCoordinatorConvert.INSTANCE.convertList(list);
        if (CollUtil.isNotEmpty(result)) {
            fillAreaNames(result);
            fillEnterpriseNames(result);
        }
        return result;
    }

    @Override
    public List<CarbonGasCoordinatorRespVO> getCoordinatorListByEnterpriseId(Long enterpriseId) {
        List<CarbonGasCoordinatorDO> coordinators = coordinatorMapper.selectByEnterpriseId(enterpriseId);
        List<CarbonGasCoordinatorRespVO> list = CarbonGasCoordinatorConvert.INSTANCE.convertList(coordinators);
        fillAreaNames(list);
        fillEnterpriseNames(list);
        return list;
    }

    @VisibleForTesting
    void validateCoordinatorExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonGasCoordinatorDO coordinator = coordinatorMapper.selectById(id);
        if (coordinator == null) {
            throw exception(GAS_COORDINATOR_NOT_EXISTS);
        }
    }

    /**
     * 批量填充协管员的负责区域名称
     * area 存储格式为 "省,市,区,街道,村"，解析每个代码查AreaApi获取名称后拼接
     */
    private void fillAreaNames(List<CarbonGasCoordinatorRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 收集所有区域中出现的区划代码
        Set<Long> allAreaIds = new HashSet<>();
        list.forEach(item -> collectAreaIds(item.getArea(), allAreaIds));
        if (allAreaIds.isEmpty()) {
            return;
        }
        Map<Long, String> areaMap = getAreaNameMap(allAreaIds);
        list.forEach(item -> item.setAreaName(buildAreaName(item.getArea(), areaMap)));
    }

    /**
     * 从逗号分隔的区域代码字符串中收集所有区划ID
     */
    private void collectAreaIds(String areaStr, Set<Long> areaIds) {
        if (StrUtil.isBlank(areaStr)) {
            return;
        }
        Arrays.stream(areaStr.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .forEach(code -> {
                    try {
                        areaIds.add(Long.parseLong(code));
                    } catch (NumberFormatException ignored) {
                    }
                });
    }

    /**
     * 根据逗号分隔的区域代码字符串，按顺序拼接区划中文名称
     * 例如 "13,1301,130102" -> "河北省/石家庄市/长安区"
     */
    private String buildAreaName(String areaStr, Map<Long, String> areaMap) {
        if (StrUtil.isBlank(areaStr)) {
            return null;
        }
        return Arrays.stream(areaStr.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .map(code -> {
                    try {
                        return areaMap.get(Long.parseLong(code));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/"));
    }

    /**
     * 填充单个协管员的企业名称
     */
    private void fillEnterpriseName(CarbonGasCoordinatorRespVO respVO) {
        if (respVO.getEnterpriseId() == null) {
            return;
        }
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectById(respVO.getEnterpriseId());
        if (enterprise != null) {
            respVO.setEnterpriseName(enterprise.getEnterpriseName());
        }
    }

    /**
     * 批量填充协管员的企业名称
     */
    private void fillEnterpriseNames(List<CarbonGasCoordinatorRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> enterpriseIds = list.stream()
                .map(CarbonGasCoordinatorRespVO::getEnterpriseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (enterpriseIds.isEmpty()) {
            return;
        }
        List<CarbonMaintenanceEnterpriseDO> enterprises = enterpriseMapper.selectBatchIds(enterpriseIds);
        Map<Long, String> enterpriseMap = CollectionUtils.convertMap(enterprises,
                CarbonMaintenanceEnterpriseDO::getId, CarbonMaintenanceEnterpriseDO::getEnterpriseName);
        list.forEach(item -> item.setEnterpriseName(enterpriseMap.get(item.getEnterpriseId())));
    }

    private Map<Long, String> getAreaNameMap(Set<Long> areaIds) {
        CommonResult<List<AreaRespDTO>> result = areaApi.getAreaList(areaIds);
        List<AreaRespDTO> areaList = result.getData();
        return CollectionUtils.convertMap(areaList, AreaRespDTO::getId, AreaRespDTO::getName);
    }
}
