package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasInfoDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonGasInfoService {

    Long createGasInfo(@Valid CarbonGasInfoSaveReqVO createReqVO);

    void updateGasInfo(@Valid CarbonGasInfoSaveReqVO updateReqVO);

    void deleteGasInfo(Long id);

    CarbonGasInfoRespVO getGasInfo(Long id);

    PageResult<CarbonGasInfoRespVO> getGasInfoPage(CarbonGasInfoPageReqVO pageReqVO);

    void importGasInfoList(List<CarbonGasInfoImportVO> list);

    String generateGasCode();

    List<CarbonGasInfoRespVO> getGasInfoList(@Valid CarbonGasInfoReqVO reqVO);

    List<CarbonGasInfoRespVO> getGasInfoListByPage(@Valid CarbonGasInfoPageReqVO pageReqVO);
}
