package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonEmissionSourcePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonEmissionSourceReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonEmissionSourceSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonEmissionSourceRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonEmissionSourceDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonEmissionSourceService {

    Long createEmissionSource(@Valid CarbonEmissionSourceSaveReqVO createReqVO);

    void updateEmissionSource(@Valid CarbonEmissionSourceSaveReqVO updateReqVO);

    void deleteEmissionSource(Long id);

    CarbonEmissionSourceRespVO getEmissionSource(Long id);

    PageResult<CarbonEmissionSourceRespVO> getEmissionSourcePage(CarbonEmissionSourcePageReqVO pageReqVO);

    String generateSourceCode();

    List<CarbonEmissionSourceRespVO> getEmissionSourceList(@Valid CarbonEmissionSourceReqVO reqVO);

    List<CarbonEmissionSourceRespVO> getEmissionSourceListByPage(@Valid CarbonEmissionSourcePageReqVO pageReqVO);
}
