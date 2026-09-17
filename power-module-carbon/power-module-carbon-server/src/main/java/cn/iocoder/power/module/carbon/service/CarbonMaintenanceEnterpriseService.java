package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterprisePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseSaveReqVO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonMaintenanceEnterpriseService {

    Long createEnterprise(@Valid CarbonMaintenanceEnterpriseSaveReqVO createReqVO);

    void updateEnterprise(@Valid CarbonMaintenanceEnterpriseSaveReqVO updateReqVO);

    void deleteEnterprise(Long id);

    CarbonMaintenanceEnterpriseRespVO getEnterprise(Long id);

    PageResult<CarbonMaintenanceEnterpriseRespVO> getEnterprisePage(CarbonMaintenanceEnterprisePageReqVO pageReqVO);

    List<CarbonMaintenanceEnterpriseRespVO> getEnterpriseList(CarbonMaintenanceEnterprisePageReqVO pageReqVO);
}
