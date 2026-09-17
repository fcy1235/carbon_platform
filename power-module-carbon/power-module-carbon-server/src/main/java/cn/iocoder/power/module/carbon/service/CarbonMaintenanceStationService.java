package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationSaveReqVO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonMaintenanceStationService {

    Long createStation(@Valid CarbonMaintenanceStationSaveReqVO createReqVO);

    void updateStation(@Valid CarbonMaintenanceStationSaveReqVO updateReqVO);

    void deleteStation(Long id);

    CarbonMaintenanceStationRespVO getStation(Long id);

    PageResult<CarbonMaintenanceStationRespVO> getStationPage(CarbonMaintenanceStationPageReqVO pageReqVO);

    List<CarbonMaintenanceStationRespVO> getStationList(CarbonMaintenanceStationPageReqVO pageReqVO);

    List<CarbonMaintenanceStationRespVO> getStationListByEnterpriseId(Long enterpriseId);
}
