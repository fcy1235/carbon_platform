package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityImportVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityDO;

import java.util.Collection;
import java.util.List;

public interface CarbonElectricityService {

    CarbonElectricityRespVO getElectricity(Long id);

    PageResult<CarbonElectricityRespVO> getElectricityPage(CarbonElectricityPageReqVO pageReqVO);

    List<CarbonElectricityRespVO> getElectricityList(CarbonElectricityPageReqVO pageReqVO);

    /**
     * 查询指定电力户号的所有记录（详情）
     */
    PageResult<CarbonElectricityRespVO> getElectricityDetail(String electricityId);

    void syncElectricity(Long id);

    void syncAllElectricity();

    /**
     * 导入电力数据
     */
    void importElectricityList(List<CarbonElectricityImportVO> list);

    /**
     * 删除电力数据（支持单条和批量）
     *
     * @param electricIds 数据ID集合
     */
    void deleteElectricityDataByElectricIds(Collection<String> electricIds);

    void deleteElectricityData(List<Long> ids);
}
