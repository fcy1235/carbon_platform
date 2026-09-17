package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.module.carbon.controller.admin.report.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonReportConvert {

    CarbonReportConvert INSTANCE = Mappers.getMapper(CarbonReportConvert.class);

    CarbonGasUsageReportExcelVO convertGasExcel(CarbonGasUsageReportRespVO bean);

    List<CarbonGasUsageReportExcelVO> convertGasExcelList(List<CarbonGasUsageReportRespVO> list);

    CarbonElectricityUsageReportExcelVO convertElectricityExcel(CarbonElectricityUsageReportRespVO bean);

    List<CarbonElectricityUsageReportExcelVO> convertElectricityExcelList(List<CarbonElectricityUsageReportRespVO> list);

    CarbonReformAccountReportExcelVO convertReformExcel(CarbonReformAccountReportRespVO bean);

    List<CarbonReformAccountReportExcelVO> convertReformExcelList(List<CarbonReformAccountReportRespVO> list);
}
