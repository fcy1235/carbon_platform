package cn.iocoder.power.module.carbon.controller.admin.report;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonReportConvert;
import cn.iocoder.power.module.carbon.service.CarbonReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;

@Tag(name = "管理后台 - 报表统计")
@RestController
@RequestMapping("/carbon/report")
@Validated
public class CarbonReportController {

    @Resource
    private CarbonReportService reportService;

    // ==================== 首页仪表盘统计 ====================

    @GetMapping("/dashboard")
    @Operation(summary = "首页仪表盘统计数据")
    public CommonResult<CarbonDashboardRespVO> getDashboard() {
        return CommonResult.success(reportService.getDashboard());
    }

    // ==================== 报表一：农村气代煤用户用气量统计 ====================

    @GetMapping("/gas-usage")
    @Operation(summary = "农村气代煤用户用气量统计")
    public CommonResult<List<CarbonGasUsageReportRespVO>> getGasUsageReport(@Valid CarbonGasUsageReportReqVO reqVO) {
        return CommonResult.success(reportService.getGasUsageReport(reqVO));
    }

    @GetMapping("/gas-usage/export")
    @Operation(summary = "导出农村气代煤用户用气量统计 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:report:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportGasUsageReport(@Valid CarbonGasUsageReportReqVO reqVO, HttpServletResponse response) throws IOException {
        List<CarbonGasUsageReportRespVO> result = reportService.getGasUsageReport(reqVO);
        List<CarbonGasUsageReportExcelVO> list = CarbonReportConvert.INSTANCE.convertGasExcelList(result);
        ExcelUtils.write(response, "农村气代煤用户用气量统计.xls", "数据", CarbonGasUsageReportExcelVO.class, list);
    }

    // ==================== 报表二：农村电代煤用户用电量统计 ====================

    @GetMapping("/electricity-usage")
    @Operation(summary = "农村电代煤用户用电量统计")
//    @PreAuthorize("@ss.hasPermission('carbon:report:query')")
    public CommonResult<List<CarbonElectricityUsageReportRespVO>> getElectricityUsageReport(@Valid CarbonElectricityUsageReportReqVO reqVO) {
        return CommonResult.success(reportService.getElectricityUsageReport(reqVO));
    }

    @GetMapping("/electricity-usage/export")
    @Operation(summary = "导出农村电代煤用户用电量统计 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:report:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportElectricityUsageReport(@Valid CarbonElectricityUsageReportReqVO reqVO, HttpServletResponse response) throws IOException {
        List<CarbonElectricityUsageReportRespVO> result = reportService.getElectricityUsageReport(reqVO);
        List<CarbonElectricityUsageReportExcelVO> list = CarbonReportConvert.INSTANCE.convertElectricityExcelList(result);
        ExcelUtils.write(response, "农村电代煤用户用电量统计.xls", "数据", CarbonElectricityUsageReportExcelVO.class, list);
    }

    // ==================== 报表三：清洁取暖改造确户台账 ====================

    @GetMapping("/reform-account")
    @Operation(summary = "清洁取暖改造确户台账")
//    @PreAuthorize("@ss.hasPermission('carbon:report:query')")
    public CommonResult<List<CarbonReformAccountReportRespVO>> getReformAccountReport(@Valid CarbonReformAccountReportReqVO reqVO) {
        return CommonResult.success(reportService.getReformAccountReport(reqVO));
    }

    @GetMapping("/reform-account/export")
    @Operation(summary = "导出清洁取暖改造确户台账 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:report:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportReformAccountReport(@Valid CarbonReformAccountReportReqVO reqVO, HttpServletResponse response) throws IOException {
        List<CarbonReformAccountReportRespVO> result = reportService.getReformAccountReport(reqVO);
        List<CarbonReformAccountReportExcelVO> list = CarbonReportConvert.INSTANCE.convertReformExcelList(result);
        ExcelUtils.write(response, "清洁取暖改造确户台账.xls", "数据", CarbonReformAccountReportExcelVO.class, list);
    }

    // ==================== 报表四：项目碳减排报表 ====================

    @GetMapping("/project-report")
    @Operation(summary = "项目碳减排报表")
    @Parameter(name = "projectId", description = "项目编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('carbon:report:query')")
    public CommonResult<CarbonProjectReportRespVO> getProjectReport(@RequestParam("projectId") Long projectId) {
        return CommonResult.success(reportService.getProjectReport(projectId));
    }


}
