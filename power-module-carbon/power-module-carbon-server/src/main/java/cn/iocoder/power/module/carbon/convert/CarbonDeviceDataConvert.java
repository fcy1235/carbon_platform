package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDataDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDeviceDataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonDeviceDataConvert {

    CarbonDeviceDataConvert INSTANCE = Mappers.getMapper(CarbonDeviceDataConvert.class);

    CarbonDeviceDataDO convert(CarbonDeviceDataSaveReqVO bean);

    CarbonDeviceDataRespVO convert(CarbonDeviceDataDO bean);

    List<CarbonDeviceDataRespVO> convertList(List<CarbonDeviceDataDO> list);

    List<CarbonDeviceDataRespVO> convertDtoList(List<CarbonDeviceDataMapper.CarbonDeviceDataWithUserDTO> list);

    @Mapping(source = "reportTime", target = "reportTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CarbonDeviceDataExcelVO convertExcel(CarbonDeviceDataRespVO bean);

    List<CarbonDeviceDataExcelVO> convertExcelList(List<CarbonDeviceDataRespVO> list);
}
