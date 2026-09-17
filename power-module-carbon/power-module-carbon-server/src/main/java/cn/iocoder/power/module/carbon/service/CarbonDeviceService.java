package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDevicePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceStatusUpdateReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSimpleRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CarbonDeviceService {

    Long createDevice(@Valid CarbonDeviceSaveReqVO createReqVO);

    void updateDevice(@Valid CarbonDeviceSaveReqVO updateReqVO);

    void deleteDevice(Long id);

    void updateDeviceStatus(@Valid CarbonDeviceStatusUpdateReqVO reqVO);

    CarbonDeviceRespVO getDevice(Long id);
    Map<Long, CarbonDeviceRespVO> getDeviceMap(Collection<Long> ids);

    PageResult<CarbonDeviceRespVO> getDevicePage(CarbonDevicePageReqVO pageReqVO);

    List<CarbonDeviceRespVO> getDeviceList(CarbonDevicePageReqVO pageReqVO);
    List<CarbonDeviceSimpleRespVO> getDeviceSimpleList(CarbonDevicePageReqVO pageReqVO);

    /**
     * 按编码前缀生成设备编码，规则：（前缀）-6位序号，如 GM-000001
     *
     * @param prefix 设备类型前缀（EM-电表、GM-燃气表、ASHP-空气源热泵）
     */
    String generateCode(String prefix);
}
