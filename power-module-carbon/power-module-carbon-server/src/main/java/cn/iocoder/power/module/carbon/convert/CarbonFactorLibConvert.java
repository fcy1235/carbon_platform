package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibGasDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonFactorLibConvert {

    CarbonFactorLibConvert INSTANCE = Mappers.getMapper(CarbonFactorLibConvert.class);

    CarbonFactorLibDO convert(CarbonFactorLibSaveReqVO bean);

    CarbonFactorLibReqVO convert(CarbonFactorLibPageReqVO bean);

    List<CarbonFactorLibGasDO> convertGasList(List<CarbonFactorLibGasSaveReqVO> list);

    CarbonFactorLibRespVO convert(CarbonFactorLibDO bean);

    List<CarbonFactorLibRespVO> convertList(List<CarbonFactorLibDO> list);

    PageResult<CarbonFactorLibRespVO> convertPage(PageResult<CarbonFactorLibDO> page);

//    @Mapping(target = "gasList", expression = "java(convertGasListToString(bean.getGasList()))")
//    CarbonFactorLibExcelVO convertExcel(CarbonFactorLibRespVO bean);

    List<CarbonFactorLibExcelVO> convertExcelList(List<CarbonFactorLibRespVO> list);

    CarbonFactorLibGasRespVO convert(CarbonFactorLibGasDO bean);

    List<CarbonFactorLibGasRespVO> convertGasRespList(List<CarbonFactorLibGasDO> list);

    default String convertGasListToString(List<CarbonFactorLibGasRespVO> gasList) {
        if (gasList == null || gasList.isEmpty()) {
            return null;
        }
        return gasList.stream()
                .map(CarbonFactorLibGasRespVO::getGasName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }
}
