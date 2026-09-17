package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceStationDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonMaintenanceStationConvert {

    CarbonMaintenanceStationConvert INSTANCE = Mappers.getMapper(CarbonMaintenanceStationConvert.class);

    CarbonMaintenanceStationDO convert(CarbonMaintenanceStationSaveReqVO bean);

    CarbonMaintenanceStationRespVO convert(CarbonMaintenanceStationDO bean);

    List<CarbonMaintenanceStationRespVO> convertList(List<CarbonMaintenanceStationDO> list);

    PageResult<CarbonMaintenanceStationRespVO> convertPage(PageResult<CarbonMaintenanceStationDO> page);

    CarbonMaintenanceStationExcelVO convertExcel(CarbonMaintenanceStationRespVO bean);

    List<CarbonMaintenanceStationExcelVO> convertExcelList(List<CarbonMaintenanceStationRespVO> list);
}
