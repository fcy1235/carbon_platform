package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataImportVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasDataDO;

import java.util.Collection;
import java.util.List;

public interface CarbonGasDataService {

    CarbonGasDataRespVO getGasData(Long id);

    PageResult<CarbonGasDataRespVO> getGasDataPage(CarbonGasDataPageReqVO pageReqVO);

    List<CarbonGasDataRespVO> getGasDataList(CarbonGasDataPageReqVO pageReqVO);

    /**
     * 查询指定燃气户号的所有记录（详情）
     */
    PageResult<CarbonGasDataRespVO> getGasDataDetail(String gasId);

    void syncGasData(Long id);

    void syncAllGasData();

    /**
     * 导入燃气数据
     */
    void importGasDataList(List<CarbonGasDataImportVO> list);

    /**
     * 删除燃气数据（支持单条和批量）
     *
     * @param ids 数据ID集合
     */
    void deleteGasData(List<Long> ids);

    /**
     * 根据燃气户号删除燃气数据
     *
     * @param gasIds 燃气户号集合
     */
    void deleteGasDataByGasId(Collection<String> gasIds);
}
