package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselinePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonBaselineDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonBaselineService {

    Long createBaseline(@Valid CarbonBaselineSaveReqVO createReqVO);

    void updateBaseline(@Valid CarbonBaselineSaveReqVO updateReqVO);

    void deleteBaseline(Long id);

    CarbonBaselineRespVO getBaseline(Long id);

    PageResult<CarbonBaselineRespVO> getBaselinePage(CarbonBaselinePageReqVO pageReqVO);

    List<CarbonBaselineRespVO> getBaselineList(CarbonBaselinePageReqVO reqVO);
}
