package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFactorLibPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFactorLibReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFactorLibSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFactorLibRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonFactorLibService {

    Long createFactorLib(@Valid CarbonFactorLibSaveReqVO createReqVO);

    void updateFactorLib(@Valid CarbonFactorLibSaveReqVO updateReqVO);

    void deleteFactorLib(Long id);

    CarbonFactorLibRespVO getFactorLib(Long id);

    List<CarbonFactorLibRespVO> getFactorLibList(CarbonFactorLibReqVO carbonFactorLibReqVO);

    List<CarbonFactorLibRespVO> getFactorLibListByIds(List<Long> ids);

    PageResult<CarbonFactorLibRespVO> getFactorLibPage(CarbonFactorLibPageReqVO pageReqVO);

    String generateFactorCode();
}
