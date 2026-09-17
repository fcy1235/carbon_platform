package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonParamLibDO;
import jakarta.validation.Valid;

public interface CarbonParamLibService {

    Long createParamLib(@Valid CarbonParamLibSaveReqVO createReqVO);

    void updateParamLib(@Valid CarbonParamLibSaveReqVO updateReqVO);

    void deleteParamLib(Long id);

    CarbonParamLibRespVO getParamLib(Long id);

    PageResult<CarbonParamLibRespVO> getParamLibPage(CarbonParamLibPageReqVO pageReqVO);
}
