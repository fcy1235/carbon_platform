package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDetailDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentParamDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonDocumentConvert {

    CarbonDocumentConvert INSTANCE = Mappers.getMapper(CarbonDocumentConvert.class);

    CarbonDocumentDO convert(CarbonDocumentSaveReqVO bean);

    CarbonDocumentReqVO convert(CarbonDocumentPageReqVO bean);

    List<CarbonDocumentDetailDO> convertDetailSaveList(List<CarbonDocumentDetailSaveReqVO> list);

    List<CarbonDocumentParamDO> convertParamSaveList(List<CarbonDocumentParamSaveReqVO> list);

    CarbonDocumentRespVO convert(CarbonDocumentDO bean);

    List<CarbonDocumentRespVO> convertList(List<CarbonDocumentDO> list);

    PageResult<CarbonDocumentRespVO> convertPage(PageResult<CarbonDocumentDO> page);

    CarbonDocumentExcelVO convertExcel(CarbonDocumentRespVO bean);

    List<CarbonDocumentExcelVO> convertExcelList(List<CarbonDocumentRespVO> list);

    CarbonDocumentDetailRespVO convert(CarbonDocumentDetailDO bean);

    List<CarbonDocumentDetailRespVO> convertDetailList(List<CarbonDocumentDetailDO> list);

    CarbonDocumentParamRespVO convert(CarbonDocumentParamDO bean);

    List<CarbonDocumentParamRespVO> convertParamList(List<CarbonDocumentParamDO> list);
}
