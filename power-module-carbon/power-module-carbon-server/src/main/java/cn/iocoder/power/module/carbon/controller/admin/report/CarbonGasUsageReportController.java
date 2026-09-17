package cn.iocoder.power.module.carbon.controller.admin.report;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportResultVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataReqVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataRespVO;
import cn.iocoder.power.module.carbon.service.CarbonGasUsageReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 燃气数据报表（采暖季用气量统计，新表）Controller
 *
 * 数据来源为 carbon_gas_usage_report 表（Excel 导入维护），
 * 与原有的 /carbon/report/gas-usage（实时联表计算）互不影响。
 */
@Tag(name = "管理后台 - 燃气数据报表（导入）")
@RestController
@RequestMapping("/carbon/gas-usage-report")
@Validated
public class CarbonGasUsageReportController {

    @Resource
    private CarbonGasUsageReportService gasUsageReportService;

    @GetMapping("/list")
    @Operation(summary = "查询燃气数据报表列表")
    public CommonResult<List<CarbonGasUsageReportDataRespVO>> getGasUsageDataList(@Valid CarbonGasUsageReportDataReqVO reqVO) {
        return CommonResult.success(gasUsageReportService.getGasUsageDataList(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出燃气数据报表 Excel")
    public void exportGasUsageDataExcel(@Valid CarbonGasUsageReportDataReqVO reqVO, HttpServletResponse response) throws IOException {
        List<CarbonGasUsageReportDataRespVO> result = gasUsageReportService.getGasUsageDataList(reqVO);
        List<CarbonGasUsageReportDataExcelVO> list = result.stream().map(item -> {
            CarbonGasUsageReportDataExcelVO excelVO = new CarbonGasUsageReportDataExcelVO();
            BeanUtils.copyProperties(item, excelVO);
            return excelVO;
        }).collect(Collectors.toList());
        ExcelUtils.write(response, "农村煤改气用户气量统计表（采暖季）.xls", "数据", CarbonGasUsageReportDataExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "下载燃气数据报表导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/燃气数据报表导入模板.xlsx");
        if (!resource.exists()) {
            throw new IllegalStateException("导入模板文件不存在，请联系管理员");
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + resource.getFilename());
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (InputStream is = resource.getInputStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    @PostMapping("/import")
    @Operation(summary = "导入燃气数据报表")
    public CommonResult<CarbonDeviceImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return CommonResult.success(gasUsageReportService.importGasUsageReport(file));
    }

}
