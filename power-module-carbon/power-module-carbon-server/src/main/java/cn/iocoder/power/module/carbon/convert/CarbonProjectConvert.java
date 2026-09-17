package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectDocDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectProgressDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonProjectConvert {

    CarbonProjectConvert INSTANCE = Mappers.getMapper(CarbonProjectConvert.class);

    CarbonProjectDO convert(CarbonProjectSaveReqVO bean);

    CarbonProjectRespVO convert(CarbonProjectDO bean);

    CarbonProjectUserDO convert(CarbonProjectUserSaveReqVO bean);

    List<CarbonProjectUserDO> convertUserList(List<CarbonProjectUserSaveReqVO> list);

    CarbonProjectUserRespVO convert(CarbonProjectUserDO bean);

    List<CarbonProjectUserRespVO> convertUserRespList(List<CarbonProjectUserDO> list);

    CarbonProjectDocRespVO convert(CarbonProjectDocDO bean);

    List<CarbonProjectDocRespVO> convertDocRespList(List<CarbonProjectDocDO> list);

    CarbonProjectProgressRespVO convert(CarbonProjectProgressDO bean);

    List<CarbonProjectProgressRespVO> convertProgressRespList(List<CarbonProjectProgressDO> list);

    List<CarbonProjectRespVO> convertList(List<CarbonProjectDO> list);

    PageResult<CarbonProjectRespVO> convertPage(PageResult<CarbonProjectDO> page);

    CarbonProjectExcelVO convertExcel(CarbonProjectRespVO bean);

    List<CarbonProjectExcelVO> convertExcelList(List<CarbonProjectRespVO> list);
}
