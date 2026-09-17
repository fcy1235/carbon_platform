package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoImportVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonUserInfoConvert {

    CarbonUserInfoConvert INSTANCE = Mappers.getMapper(CarbonUserInfoConvert.class);

    CarbonUserInfoDO convert(CarbonUserInfoSaveReqVO bean);

    CarbonUserInfoDO convert(CarbonUserInfoImportVO bean);

    CarbonUserInfoRespVO convert(CarbonUserInfoDO bean);

    List<CarbonUserInfoRespVO> convertList(List<CarbonUserInfoDO> list);

    PageResult<CarbonUserInfoRespVO> convertPage(PageResult<CarbonUserInfoDO> page);

    CarbonUserInfoExcelVO convertExcel(CarbonUserInfoRespVO bean);

    List<CarbonUserInfoExcelVO> convertExcelList(List<CarbonUserInfoRespVO> list);
}
