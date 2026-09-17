package cn.iocoder.power.module.carbon.controller.admin.report;

import cn.hutool.core.io.IoUtil;
import cn.idev.excel.EasyExcel;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataImportVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataRespVO;
import cn.iocoder.power.module.carbon.convert.CarbonElectricityUsageReportDataConvert;
import cn.iocoder.power.module.carbon.service.CarbonElectricityUsageReportDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Tag(name = "管理后台 - 电力用电量报表")
@RestController
@RequestMapping("/carbon/electricity-usage-report")
@Validated
public class CarbonElectricityUsageReportController {

    @Resource
    private CarbonElectricityUsageReportDataService electricityUsageReportDataService;

    @GetMapping("/list")
    @Operation(summary = "获得电力用电量报表分页")
    @PreAuthorize("@ss.hasPermission('carbon:electricity-usage-report:query')")
    public CommonResult<PageResult<CarbonElectricityUsageReportDataRespVO>> getElectricityUsageReportDataPage(
            @Valid CarbonElectricityUsageReportDataPageReqVO pageReqVO) {
        return CommonResult.success(electricityUsageReportDataService.getElectricityUsageReportDataPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出电力用电量报表 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:electricity-usage-report:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportElectricityUsageReportDataExcel(
            @Valid CarbonElectricityUsageReportDataPageReqVO listReqVO, HttpServletResponse response) throws IOException {
        List<CarbonElectricityUsageReportDataRespVO> result = electricityUsageReportDataService.getElectricityUsageReportDataList(listReqVO);
        List<CarbonElectricityUsageReportDataExcelVO> list = CarbonElectricityUsageReportDataConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "电力用电量报表.xls", "数据", CarbonElectricityUsageReportDataExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得电力用电量报表导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/电力用电量报表导入模板.xlsx");
        if (!resource.exists()) {
            throw new IllegalStateException("导入模板文件不存在，请联系管理员");
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + resource.getFilename());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        try (InputStream is = resource.getInputStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    @PostMapping("/import")
    @Operation(summary = "导入电力用电量报表数据")
    @PreAuthorize("@ss.hasPermission('carbon:electricity-usage-report:import')")
    @ApiAccessLog(operateType = OperateTypeEnum.IMPORT)
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        // 模板第 1 行为合并标题行、第 2 行为列头，headRowNumber=2 跳过
        List<CarbonElectricityUsageReportDataImportVO> list;
        try (InputStream is = file.getInputStream()) {
            list = EasyExcel.read(is, CarbonElectricityUsageReportDataImportVO.class, null)
                    .sheet()
                    .headRowNumber(2)
                    .doReadSync();
        }
        electricityUsageReportDataService.importElectricityUsageReportDataList(list);
        return CommonResult.success("导入成功");
    }

}
