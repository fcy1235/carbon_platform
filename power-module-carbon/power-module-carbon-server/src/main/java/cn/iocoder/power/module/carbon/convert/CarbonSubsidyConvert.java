package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyRespVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidySaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonSubsidyDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonSubsidyWithUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonSubsidyConvert {

    CarbonSubsidyConvert INSTANCE = Mappers.getMapper(CarbonSubsidyConvert.class);

    CarbonSubsidyDO convert(CarbonSubsidySaveReqVO bean);

    CarbonSubsidyRespVO convert(CarbonSubsidyDO bean);

    List<CarbonSubsidyRespVO> convertList(List<CarbonSubsidyDO> list);

    PageResult<CarbonSubsidyRespVO> convertPage(PageResult<CarbonSubsidyDO> page);

    CarbonSubsidyExcelVO convertExcel(CarbonSubsidyRespVO bean);

    List<CarbonSubsidyExcelVO> convertExcelList(List<CarbonSubsidyRespVO> list);

    /**
     * 联表查询结果转换
     */
    CarbonSubsidyRespVO convertFromWithUser(CarbonSubsidyWithUserDO bean);

    List<CarbonSubsidyRespVO> convertListFromWithUser(List<CarbonSubsidyWithUserDO> list);

    PageResult<CarbonSubsidyRespVO> convertPageFromWithUser(PageResult<CarbonSubsidyWithUserDO> page);

    /**
     * 联表查询结果直接转Excel VO
     */
    CarbonSubsidyExcelVO convertExcelFromWithUser(CarbonSubsidyWithUserDO bean);

    List<CarbonSubsidyExcelVO> convertExcelListFromWithUser(List<CarbonSubsidyWithUserDO> list);
}
