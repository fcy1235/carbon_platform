package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasDataRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasDataDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasDataMapper;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonGasDataConvert {

    CarbonGasDataConvert INSTANCE = Mappers.getMapper(CarbonGasDataConvert.class);

    CarbonGasDataRespVO convert(CarbonGasDataDO bean);

    List<CarbonGasDataRespVO> convertList(List<CarbonGasDataDO> list);

    PageResult<CarbonGasDataRespVO> convertPage(PageResult<CarbonGasDataDO> page);

    CarbonGasDataRespVO convertDto(CarbonGasDataMapper.CarbonGasDataWithUserDTO dto);

    List<CarbonGasDataRespVO> convertDtoList(List<CarbonGasDataMapper.CarbonGasDataWithUserDTO> list);

    CarbonGasDataExcelVO convertExcel(CarbonGasDataRespVO bean);

    List<CarbonGasDataExcelVO> convertExcelList(List<CarbonGasDataRespVO> list);
}
