package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSimpleRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonDeviceConvert {

    CarbonDeviceConvert INSTANCE = Mappers.getMapper(CarbonDeviceConvert.class);

    CarbonDeviceDO convert(CarbonDeviceSaveReqVO bean);

    CarbonDeviceRespVO convert(CarbonDeviceDO bean);

    CarbonDeviceSimpleRespVO convertSimple(CarbonDeviceDO bean);

    List<CarbonDeviceSimpleRespVO> convertSimpleList(List<CarbonDeviceDO> list);

    List<CarbonDeviceRespVO> convertList(List<CarbonDeviceDO> list);

    PageResult<CarbonDeviceRespVO> convertPage(PageResult<CarbonDeviceDO> page);

    CarbonDeviceExcelVO convertExcel(CarbonDeviceRespVO bean);

    List<CarbonDeviceExcelVO> convertExcelList(List<CarbonDeviceRespVO> list);
}
