package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonGasInfoConvert {

    CarbonGasInfoConvert INSTANCE = Mappers.getMapper(CarbonGasInfoConvert.class);

    CarbonGasInfoDO convert(CarbonGasInfoSaveReqVO bean);

    CarbonGasInfoReqVO convert(CarbonGasInfoPageReqVO bean);

    CarbonGasInfoDO convert(CarbonGasInfoImportVO bean);

    CarbonGasInfoRespVO convert(CarbonGasInfoDO bean);

    List<CarbonGasInfoRespVO> convertList(List<CarbonGasInfoDO> list);

    PageResult<CarbonGasInfoRespVO> convertPage(PageResult<CarbonGasInfoDO> page);

    CarbonGasInfoExcelVO convertExcel(CarbonGasInfoRespVO bean);

    List<CarbonGasInfoExcelVO> convertExcelList(List<CarbonGasInfoRespVO> list);
}
