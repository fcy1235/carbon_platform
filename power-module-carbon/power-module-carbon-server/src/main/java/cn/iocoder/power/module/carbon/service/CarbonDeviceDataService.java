package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataSaveReqVO;

import java.util.List;

public interface CarbonDeviceDataService {

    /**
     * 新增设备数据（外部接口上报写入）
     */
    Long createDeviceData(CarbonDeviceDataSaveReqVO createReqVO);

    CarbonDeviceDataRespVO getDeviceData(Long id);

    /**
     * 分页查询设备数据（每设备最新一条，联用户信息）
     */
    PageResult<CarbonDeviceDataRespVO> getDeviceDataPage(CarbonDeviceDataPageReqVO pageReqVO);

    /**
     * 列表查询设备数据（每设备最新一条，用于导出）
     */
    List<CarbonDeviceDataRespVO> getDeviceDataList(CarbonDeviceDataPageReqVO pageReqVO);

    /**
     * 查询指定设备编码的所有上报记录（详情）
     */
    PageResult<CarbonDeviceDataRespVO> getDeviceDataDetail(String deviceCode);

    /**
     * 删除设备数据（支持单条和批量）
     *
     * @param ids 数据ID集合
     */
    void deleteDeviceData(List<Long> ids);
}
