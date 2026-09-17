package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasSafetyOfficerDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonGasSafetyOfficerConvert {

    CarbonGasSafetyOfficerConvert INSTANCE = Mappers.getMapper(CarbonGasSafetyOfficerConvert.class);

    CarbonGasSafetyOfficerDO convert(CarbonGasSafetyOfficerSaveReqVO bean);

    CarbonGasSafetyOfficerRespVO convert(CarbonGasSafetyOfficerDO bean);

    List<CarbonGasSafetyOfficerRespVO> convertList(List<CarbonGasSafetyOfficerDO> list);

    PageResult<CarbonGasSafetyOfficerRespVO> convertPage(PageResult<CarbonGasSafetyOfficerDO> page);

    CarbonGasSafetyOfficerExcelVO convertExcel(CarbonGasSafetyOfficerRespVO bean);

    List<CarbonGasSafetyOfficerExcelVO> convertExcelList(List<CarbonGasSafetyOfficerRespVO> list);
}
