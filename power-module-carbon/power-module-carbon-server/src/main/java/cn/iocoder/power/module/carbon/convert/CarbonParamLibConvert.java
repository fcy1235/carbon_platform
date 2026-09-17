package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonParamLibDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonParamLibConvert {

    CarbonParamLibConvert INSTANCE = Mappers.getMapper(CarbonParamLibConvert.class);

    CarbonParamLibDO convert(CarbonParamLibSaveReqVO bean);

    CarbonParamLibRespVO convert(CarbonParamLibDO bean);

    List<CarbonParamLibRespVO> convertList(List<CarbonParamLibDO> list);

    PageResult<CarbonParamLibRespVO> convertPage(PageResult<CarbonParamLibDO> page);
}
