package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorSaveReqVO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonGasCoordinatorService {

    Long createCoordinator(@Valid CarbonGasCoordinatorSaveReqVO createReqVO);

    void updateCoordinator(@Valid CarbonGasCoordinatorSaveReqVO updateReqVO);

    void deleteCoordinator(Long id);

    CarbonGasCoordinatorRespVO getCoordinator(Long id);

    PageResult<CarbonGasCoordinatorRespVO> getCoordinatorPage(CarbonGasCoordinatorPageReqVO pageReqVO);

    List<CarbonGasCoordinatorRespVO> getCoordinatorList(CarbonGasCoordinatorPageReqVO pageReqVO);

    List<CarbonGasCoordinatorRespVO> getCoordinatorListByEnterpriseId(Long enterpriseId);
}
