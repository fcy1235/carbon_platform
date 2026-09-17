package cn.iocoder.power.module.carbon.controller.admin.ledger;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonLedgerImportConvert;
import cn.iocoder.power.module.carbon.service.CarbonLedgerImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;

@Tag(name = "管理后台 - 导入任务")
@RestController
@RequestMapping("/carbon/ledger-import")
@Validated
public class CarbonLedgerImportController {

    @Resource
    private CarbonLedgerImportService ledgerImportService;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "创建导入任务（检测阶段）")
    @Parameters({
            @Parameter(name = "uploadType", description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", required = true, example = "1"),
            @Parameter(name = "file", description = "导入文件，仅支持 .xls/.xlsx", required = true),
            @Parameter(name = "cityCode", description = "所属市编码", required = true, example = "1301"),
            @Parameter(name = "districtCode", description = "所属区县编码", required = true, example = "130108")
    })
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:create')")
    public CommonResult<Long> createImportTask(
            @RequestPart("uploadType") String uploadType,
            @RequestPart("file") MultipartFile file,
            @RequestPart("cityCode") String cityCode,
            @RequestPart("districtCode") String districtCode) throws Exception {
        Long id = ledgerImportService.createImportTask(uploadType, file, cityCode, districtCode);
        return CommonResult.success(id);
    }

    @GetMapping("/get")
    @Operation(summary = "获得导入任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:query')")
    public CommonResult<CarbonLedgerImportTaskRespVO> getImportTask(@RequestParam("id") Long id) {
        return CommonResult.success(ledgerImportService.getImportTask(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得导入任务分页")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:query')")
    @DataPermission
    public CommonResult<PageResult<CarbonLedgerImportTaskRespVO>> getImportTaskPage(@Valid CarbonLedgerImportTaskPageReqVO pageReqVO) {
        return CommonResult.success(ledgerImportService.getImportTaskPage(pageReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除导入任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:delete')")
    public CommonResult<Boolean> deleteImportTask(@RequestParam("id") Long id) {
        ledgerImportService.deleteImportTask(id);
        return CommonResult.success(true);
    }

    @PostMapping("/execute")
    @Operation(summary = "执行导入")
    @Parameter(name = "taskId", description = "任务ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:execute')")
    public CommonResult<Boolean> executeImport(@RequestParam("taskId") Long taskId) throws Exception {
        ledgerImportService.executeImport(taskId);
        return CommonResult.success(true);
    }

    @GetMapping("/detail-page")
    @Operation(summary = "获得导入明细分页")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:query')")
    public CommonResult<PageResult<CarbonLedgerImportDetailRespVO>> getImportDetailPage(@Valid CarbonLedgerImportDetailPageReqVO pageReqVO) {
        return CommonResult.success(ledgerImportService.getImportDetailPage(pageReqVO));
    }

    @GetMapping("/detail-list")
    @Operation(summary = "获得导入明细列表")
    @Parameter(name = "taskId", description = "任务ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:query')")
    public CommonResult<List<CarbonLedgerImportDetailRespVO>> getImportDetailList(@RequestParam("taskId") Long taskId) {
        return CommonResult.success(ledgerImportService.getImportDetailList(taskId));
    }

    @GetMapping("/export-errors")
    @Operation(summary = "导出异常项")
    @Parameter(name = "taskId", description = "任务ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportErrorDetails(@RequestParam("taskId") Long taskId, HttpServletResponse response) throws IOException {
        List<CarbonLedgerImportDetailRespVO> result = ledgerImportService.getFailDetailList(taskId);
        List<CarbonLedgerImportDetailExcelVO> list = CarbonLedgerImportConvert.INSTANCE.convertDetailExcelList(result);
        ExcelUtils.write(response, "导入异常项.xls", "数据", CarbonLedgerImportDetailExcelVO.class, list);
    }

    @GetMapping("/download-template")
    @Operation(summary = "下载导入模板")
    @Parameter(name = "type", description = "模板类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", required = true, example = "1")
    public void downloadTemplate(@RequestParam("type") String type, HttpServletResponse response) throws IOException {
        String templateName = getTemplateName(type);
        ClassPathResource resource = new ClassPathResource("import-template/ledger/" + templateName);
        if (!resource.exists()) {
            throw new IllegalStateException("导入模板文件不存在，请联系管理员");
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + resource.getFilename());
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (InputStream is = resource.getInputStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    @GetMapping("/download")
    @Operation(summary = "下载导入原文件")
    @Parameter(name = "id", description = "任务ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-import:query')")
    public void downloadFile(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
        CarbonLedgerImportTaskRespVO task = ledgerImportService.getImportTask(id);
        if (task == null || StrUtil.isBlank(task.getFileUrl())) {
            throw new IllegalStateException("导入任务或文件不存在");
        }
        String downloadUrl = task.getFileUrl();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(task.getFileName(), StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (InputStream is = new java.net.URL(downloadUrl).openStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    private String getTemplateName(String type) {
        return switch (type) {
            case "1" -> "新增用户信息模板.xlsx";
            case "2" -> "变更户模板.xlsx";
            case "3" -> "撤销户模板.xlsx";
            case "4" -> "改造类型变更模板.xlsx";
            default -> "新增用户信息模板.xlsx";
        };
    }
}
