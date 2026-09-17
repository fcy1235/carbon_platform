package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonContactDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonContactConvert {

    CarbonContactConvert INSTANCE = Mappers.getMapper(CarbonContactConvert.class);

    CarbonContactDO convert(CarbonContactSaveReqVO bean);

    CarbonContactRespVO convert(CarbonContactDO bean);

    List<CarbonContactRespVO> convertList(List<CarbonContactDO> list);

    PageResult<CarbonContactRespVO> convertPage(PageResult<CarbonContactDO> page);

    CarbonContactExcelVO convertExcel(CarbonContactRespVO bean);

    List<CarbonContactExcelVO> convertExcelList(List<CarbonContactRespVO> list);
}
