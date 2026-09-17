package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.*;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingActivityDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonAccountingConvert {

    CarbonAccountingConvert INSTANCE = Mappers.getMapper(CarbonAccountingConvert.class);

    CarbonAccountingDO convert(CarbonAccountingSaveReqVO bean);

    CarbonAccountUserInfoRespVO convert(CarbonUserInfoRespVO bean);

    CarbonAccountingActivityDO convert(CarbonAccountingActivitySaveReqVO bean);

    List<CarbonAccountingActivityDO> convertActivityList(List<CarbonAccountingActivitySaveReqVO> list);

    CarbonAccountingRespVO convert(CarbonAccountingDO bean);

    CarbonAccountingActivityRespVO convert(CarbonAccountingActivityDO bean);

    List<CarbonAccountingActivityRespVO> convertActivityRespList(List<CarbonAccountingActivityDO> list);

    PageResult<CarbonAccountingRespVO> convertPage(PageResult<CarbonAccountingDO> page);

    List<CarbonAccountingRespVO> convertList(List<CarbonAccountingDO> list);




    List<CarbonAccountingExcelVO> convertExcelList(List<CarbonAccountingRespVO> list);
}
