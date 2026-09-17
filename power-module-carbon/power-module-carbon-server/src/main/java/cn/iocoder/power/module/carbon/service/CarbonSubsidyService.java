package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyImportVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidySaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyRespVO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonSubsidyService {

    Long createSubsidy(@Valid CarbonSubsidySaveReqVO createReqVO);

    void updateSubsidy(@Valid CarbonSubsidySaveReqVO updateReqVO);

    void deleteSubsidy(Long id);

    CarbonSubsidyRespVO getSubsidy(Long id);

    PageResult<CarbonSubsidyRespVO> getSubsidyPage(CarbonSubsidyPageReqVO pageReqVO);

    /**
     * 获取补贴列表（支持全部导出、筛选导出、选中导出）
     */
    List<CarbonSubsidyRespVO> getSubsidyList(CarbonSubsidyPageReqVO reqVO);

    /**
     * 导入补贴数据
     */
    void importSubsidyList(List<CarbonSubsidyImportVO> list);
}
