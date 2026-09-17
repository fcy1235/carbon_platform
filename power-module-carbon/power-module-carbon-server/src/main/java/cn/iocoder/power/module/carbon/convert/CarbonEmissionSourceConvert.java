package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonEmissionSourceDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonEmissionSourceConvert {

    CarbonEmissionSourceConvert INSTANCE = Mappers.getMapper(CarbonEmissionSourceConvert.class);

    CarbonEmissionSourceDO convert(CarbonEmissionSourceSaveReqVO bean);

    CarbonEmissionSourceReqVO convert(CarbonEmissionSourcePageReqVO bean);

    CarbonEmissionSourceRespVO convert(CarbonEmissionSourceDO bean);

    List<CarbonEmissionSourceRespVO> convertList(List<CarbonEmissionSourceDO> list);

    PageResult<CarbonEmissionSourceRespVO> convertPage(PageResult<CarbonEmissionSourceDO> page);

    CarbonEmissionSourceExcelVO convertExcel(CarbonEmissionSourceRespVO bean);

    List<CarbonEmissionSourceExcelVO> convertExcelList(List<CarbonEmissionSourceRespVO> list);
}
