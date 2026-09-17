package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonMaintenanceStationConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceStationDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceEnterpriseMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceStationMapper;
import cn.iocoder.power.module.carbon.service.CarbonMaintenanceStationService;
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
public class CarbonMaintenanceStationServiceImpl implements CarbonMaintenanceStationService {

    @Resource
    private CarbonMaintenanceStationMapper stationMapper;

    @Resource
    private CarbonMaintenanceEnterpriseMapper enterpriseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStation(CarbonMaintenanceStationSaveReqVO createReqVO) {
        CarbonMaintenanceStationDO station = CarbonMaintenanceStationConvert.INSTANCE.convert(createReqVO);
        stationMapper.insert(station);
        return station.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStation(CarbonMaintenanceStationSaveReqVO updateReqVO) {
        validateStationExists(updateReqVO.getId());
        CarbonMaintenanceStationDO updateObj = CarbonMaintenanceStationConvert.INSTANCE.convert(updateReqVO);
        stationMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStation(Long id) {
        CarbonMaintenanceStationDO station = stationMapper.selectById(id);
        if (station == null) {
            throw exception(MAINTENANCE_STATION_NOT_EXISTS);
        }
        stationMapper.deleteById(id);
    }

    @Override
    public CarbonMaintenanceStationRespVO getStation(Long id) {
        CarbonMaintenanceStationDO station = stationMapper.selectById(id);
        if (station == null) {
            return null;
        }
        CarbonMaintenanceStationRespVO respVO = CarbonMaintenanceStationConvert.INSTANCE.convert(station);
        // 填充企业名称
        fillEnterpriseName(respVO);
        return respVO;
    }

    @Override
    public PageResult<CarbonMaintenanceStationRespVO> getStationPage(CarbonMaintenanceStationPageReqVO pageReqVO) {
        PageResult<CarbonMaintenanceStationDO> pageResult = stationMapper.selectPage(pageReqVO);
        PageResult<CarbonMaintenanceStationRespVO> resultVo = CarbonMaintenanceStationConvert.INSTANCE.convertPage(pageResult);
        if (CollUtil.isNotEmpty(resultVo.getList())) {
            fillEnterpriseNames(resultVo.getList());
        }
        return resultVo;
    }

    @Override
    public List<CarbonMaintenanceStationRespVO> getStationList(CarbonMaintenanceStationPageReqVO pageReqVO) {
        List<CarbonMaintenanceStationDO> list = stationMapper.selectList(pageReqVO);
        List<CarbonMaintenanceStationRespVO> result = CarbonMaintenanceStationConvert.INSTANCE.convertList(list);
        if (CollUtil.isNotEmpty(result)) {
            fillEnterpriseNames(result);
            fillServiceScopeNames(result);
        }
        return result;
    }

    @Override
    public List<CarbonMaintenanceStationRespVO> getStationListByEnterpriseId(Long enterpriseId) {
        List<CarbonMaintenanceStationDO> stations = stationMapper.selectByEnterpriseId(enterpriseId);
        List<CarbonMaintenanceStationRespVO> list = CarbonMaintenanceStationConvert.INSTANCE.convertList(stations);
        // 批量填充企业名称
        fillEnterpriseNames(list);
        return list;
    }

    @VisibleForTesting
    void validateStationExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonMaintenanceStationDO station = stationMapper.selectById(id);
        if (station == null) {
            throw exception(MAINTENANCE_STATION_NOT_EXISTS);
        }
    }

    /**
     * 填充单个网点的企业名称
     */
    private void fillEnterpriseName(CarbonMaintenanceStationRespVO respVO) {
        if (respVO.getEnterpriseId() == null) {
            return;
        }
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectById(respVO.getEnterpriseId());
        if (enterprise != null) {
            respVO.setEnterpriseName(enterprise.getEnterpriseName());
        }
    }

    /**
     * 批量填充网点的企业名称
     */
    private void fillEnterpriseNames(List<CarbonMaintenanceStationRespVO> list) {
        Set<Long> enterpriseIds = list.stream()
                .map(CarbonMaintenanceStationRespVO::getEnterpriseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (enterpriseIds.isEmpty()) {
            return;
        }
        List<CarbonMaintenanceEnterpriseDO> enterprises = enterpriseMapper.selectBatchIds(enterpriseIds);
        Map<Long, String> enterpriseNameMap = CollectionUtils.convertMap(
                enterprises, CarbonMaintenanceEnterpriseDO::getId, CarbonMaintenanceEnterpriseDO::getEnterpriseName);
        list.forEach(item -> item.setEnterpriseName(enterpriseNameMap.get(item.getEnterpriseId())));
    }

    /**
     * 批量填充网点的服务范围名称（从 serviceScope JSON 中提取 label，逗号拼接）
     */
    private void fillServiceScopeNames(List<CarbonMaintenanceStationRespVO> list) {
        list.forEach(item -> {
            if (StrUtil.isBlank(item.getServiceScope())) {
                return;
            }
            JSONArray arr = JSONUtil.parseArray(item.getServiceScope());
            String labels = arr.stream()
                    .map(obj -> ((JSONObject) obj).getStr("label"))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.joining("、"));
            item.setServiceScope(labels);
        });
    }


}
