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
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonLedgerFileConvert;
import cn.iocoder.power.module.carbon.service.CarbonLedgerFileService;
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

@Tag(name = "管理后台 - 台账审定")
@RestController
@RequestMapping("/carbon/ledger-file")
@Validated
public class CarbonLedgerFileController {

    @Resource
    private CarbonLedgerFileService ledgerFileService;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "创建台账文件")
    @Parameters({
            @Parameter(name = "uploadType", description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", required = true, example = "1"),
            @Parameter(name = "file", description = "台账文件，仅支持 .xls/.xlsx", required = true),
            @Parameter(name = "cityCode", description = "所属市编码", required = true, example = "1301"),
            @Parameter(name = "districtCode", description = "所属区县编码", required = true, example = "130108"),
            @Parameter(name = "dataLevel", description = "数据层级：1-县级 2-市级", required = true, example = "1")
    })
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:create')")
    public CommonResult<Long> createLedgerFile(
            @RequestPart("uploadType") String uploadType,
            @RequestPart("file") MultipartFile file,
            @RequestPart("cityCode") String cityCode,
            @RequestPart("districtCode") String districtCode,
            @RequestPart("dataLevel") String dataLevel) throws Exception {
        Long id = ledgerFileService.createLedgerFile(uploadType, file, cityCode, districtCode, dataLevel);
        return CommonResult.success(id);
    }

    @PutMapping(value = "/update", consumes = "multipart/form-data")
    @Operation(summary = "更新台账文件")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true, example = "1024"),
            @Parameter(name = "uploadType", description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", required = true, example = "1"),
            @Parameter(name = "file", description = "台账文件，仅支持 .xls/.xlsx")
    })
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:update')")
    public CommonResult<Boolean> updateLedgerFile(
            @RequestParam("id") Long id,
            @RequestPart("uploadType") String uploadType,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        ledgerFileService.updateLedgerFile(id, uploadType, file);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除台账文件")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:delete')")
    public CommonResult<Boolean> deleteLedgerFile(@RequestParam("id") Long id) {
        ledgerFileService.deleteLedgerFile(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得台账文件")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:query')")
    public CommonResult<CarbonLedgerFileRespVO> getLedgerFile(@RequestParam("id") Long id) {
        return CommonResult.success(ledgerFileService.getLedgerFile(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得台账文件分页")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:query')")
    @DataPermission
    public CommonResult<PageResult<CarbonLedgerFileRespVO>> getLedgerFilePage(@Valid CarbonLedgerFilePageReqVO pageReqVO) {
        return CommonResult.success(ledgerFileService.getLedgerFilePage(pageReqVO));
    }

    @GetMapping("/available-for-report")
    @Operation(summary = "获得可用于上报的台账文件")
    @Parameters({
            @Parameter(name = "uploadType", description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更", required = true, example = "1"),
            @Parameter(name = "districtCode", description = "所属区县编码", example = "130102")
    })
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:query')")
    public CommonResult<List<CarbonLedgerFileRespVO>> getAvailableFilesForReport(
            @RequestParam("uploadType") String uploadType,
            @RequestParam(value = "districtCode", required = false) String districtCode) {
        return CommonResult.success(ledgerFileService.getAvailableFilesForReport(uploadType, districtCode));
    }

    @PutMapping("/audit-status")
    @Operation(summary = "修改台账文件审核状态")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:update')")
    public CommonResult<Boolean> updateAuditStatus(@Valid @RequestBody CarbonLedgerFileAuditReqVO reqVO) {
        ledgerFileService.updateAuditStatus(reqVO.getId(), reqVO.getAuditStatus(), reqVO.getRejectReason());
        return CommonResult.success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出台账文件 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    @DataPermission
    public void exportLedgerFileExcel(@Valid CarbonLedgerFilePageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonLedgerFileRespVO> result = ledgerFileService.getLedgerFileList(pageReqVO);
        List<CarbonLedgerFileExcelVO> list = CarbonLedgerFileConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "台账审定.xls", "数据", CarbonLedgerFileExcelVO.class, list);
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
    @Operation(summary = "下载台账原文件")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-file:query')")
    public void downloadFile(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
        CarbonLedgerFileRespVO file = ledgerFileService.getLedgerFile(id);
        if (file == null || StrUtil.isBlank(file.getFileUrl())) {
            throw new IllegalStateException("台账文件不存在");
        }
        String downloadUrl =  file.getFileUrl();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (InputStream is = new java.net.URL(downloadUrl).openStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    @GetMapping("/current-address")
    @Operation(summary = "获取当前登录用户行政区地址信息")
    public CommonResult<LoginUserAddressRespVO> getLoginUserAddress() {
        String address = SecurityFrameworkUtils.getLoginUserAddress();
        LoginUserAddressRespVO respVO = new LoginUserAddressRespVO();
        if (StrUtil.isBlank(address)) {
            return CommonResult.success(respVO);
        }
        // 解析 "provinceCode|cityCode|districtCode" 格式
        String[] parts = address.split("\\|");
        if (parts.length > 0 && StrUtil.isNotBlank(parts[0])) {
            respVO.setProvinceCode(Long.parseLong(parts[0]));
        }
        if (parts.length > 1 && StrUtil.isNotBlank(parts[1])) {
            respVO.setCityCode(Long.parseLong(parts[1]));
        }
        if (parts.length > 2 && StrUtil.isNotBlank(parts[2])) {
            respVO.setDistrictCode(Long.parseLong(parts[2]));
        }
        return CommonResult.success(respVO);
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
