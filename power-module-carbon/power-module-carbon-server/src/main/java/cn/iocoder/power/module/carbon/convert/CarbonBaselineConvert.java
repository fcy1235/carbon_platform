package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonBaselineDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonBaselineConvert {

    CarbonBaselineConvert INSTANCE = Mappers.getMapper(CarbonBaselineConvert.class);

    CarbonBaselineDO convert(CarbonBaselineSaveReqVO bean);

    CarbonBaselineRespVO convert(CarbonBaselineDO bean);

    List<CarbonBaselineRespVO> convertList(List<CarbonBaselineDO> list);

    PageResult<CarbonBaselineRespVO> convertPage(PageResult<CarbonBaselineDO> page);

    CarbonBaselineExcelVO convertExcel(CarbonBaselineRespVO bean);

    List<CarbonBaselineExcelVO> convertExcelList(List<CarbonBaselineRespVO> list);
}
