package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonElectricityRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import cn.iocoder.power.module.carbon.dal.mysql.CarbonElectricityMapper;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonElectricityConvert {

    CarbonElectricityConvert INSTANCE = Mappers.getMapper(CarbonElectricityConvert.class);

    CarbonElectricityRespVO convert(CarbonElectricityDO bean);

    List<CarbonElectricityRespVO> convertList(List<CarbonElectricityDO> list);

    PageResult<CarbonElectricityRespVO> convertPage(PageResult<CarbonElectricityDO> page);

    CarbonElectricityRespVO convertDto(CarbonElectricityMapper.CarbonElectricityWithUserDTO dto);

    List<CarbonElectricityRespVO> convertDtoList(List<CarbonElectricityMapper.CarbonElectricityWithUserDTO> list);

    CarbonElectricityExcelVO convertExcel(CarbonElectricityRespVO bean);

    List<CarbonElectricityExcelVO> convertExcelList(List<CarbonElectricityRespVO> list);
}
