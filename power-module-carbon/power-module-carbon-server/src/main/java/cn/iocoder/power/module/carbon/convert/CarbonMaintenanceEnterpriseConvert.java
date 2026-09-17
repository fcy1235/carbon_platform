package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonMaintenanceEnterpriseConvert {

    CarbonMaintenanceEnterpriseConvert INSTANCE = Mappers.getMapper(CarbonMaintenanceEnterpriseConvert.class);

    CarbonMaintenanceEnterpriseDO convert(CarbonMaintenanceEnterpriseSaveReqVO bean);

    CarbonMaintenanceEnterpriseRespVO convert(CarbonMaintenanceEnterpriseDO bean);

    List<CarbonMaintenanceEnterpriseRespVO> convertList(List<CarbonMaintenanceEnterpriseDO> list);

    PageResult<CarbonMaintenanceEnterpriseRespVO> convertPage(PageResult<CarbonMaintenanceEnterpriseDO> page);

    CarbonMaintenanceEnterpriseExcelVO convertExcel(CarbonMaintenanceEnterpriseRespVO bean);

    List<CarbonMaintenanceEnterpriseExcelVO> convertExcelList(List<CarbonMaintenanceEnterpriseRespVO> list);
}
