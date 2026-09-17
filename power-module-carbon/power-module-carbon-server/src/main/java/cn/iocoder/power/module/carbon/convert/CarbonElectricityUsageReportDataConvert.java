package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataRespVO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonElectricityUsageReportDataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonElectricityUsageReportDataConvert {

    CarbonElectricityUsageReportDataConvert INSTANCE = Mappers.getMapper(CarbonElectricityUsageReportDataConvert.class);

    CarbonElectricityUsageReportDataRespVO convertDto(CarbonElectricityUsageReportDataMapper.CarbonElectricityUsageReportDataRespDTO dto);

    List<CarbonElectricityUsageReportDataRespVO> convertDtoList(List<CarbonElectricityUsageReportDataMapper.CarbonElectricityUsageReportDataRespDTO> list);

    PageResult<CarbonElectricityUsageReportDataRespVO> convertPage(PageResult<CarbonElectricityUsageReportDataMapper.CarbonElectricityUsageReportDataRespDTO> page);

    CarbonElectricityUsageReportDataExcelVO convertExcel(CarbonElectricityUsageReportDataRespVO vo);

    List<CarbonElectricityUsageReportDataExcelVO> convertExcelList(List<CarbonElectricityUsageReportDataRespVO> list);

}
