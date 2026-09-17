package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonDocumentConvert;

import cn.iocoder.power.module.carbon.service.CarbonDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 碳核算标准库管理")
@RestController
@RequestMapping("/carbon/document")
@Validated
public class CarbonDocumentController {

    @Resource
    private CarbonDocumentService documentService;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @PostMapping("/create")
    @Operation(summary = "创建文档")
    @PreAuthorize("@ss.hasPermission('carbon:document:create')")
    public CommonResult<Long> createDocument(@Valid @RequestPart("data") CarbonDocumentSaveReqVO createReqVO,
                                             @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        // 如果有文件上传，先上传文件并设置文件URL
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String path = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);
            createReqVO.setFileUrl(CarbonFileMinioUploadVO.of(minioProperties, fileName, path).getUrl());
        }
        Long id = documentService.createDocument(createReqVO);
        return CommonResult.success(id);
    }

    @GetMapping("/getCode")
    @Operation(summary = "获取编码")
    public CommonResult<String> generateCode() {
        String emissionSourceCode = documentService.generateCode();
        return CommonResult.success(emissionSourceCode);
    }

    @PutMapping("/update")
    @Operation(summary = "更新文档")
    @PreAuthorize("@ss.hasPermission('carbon:document:update')")
    public CommonResult<Boolean> updateDocument(@Valid @RequestPart("data") CarbonDocumentSaveReqVO updateReqVO,
                                                @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        // 如果有文件上传，先上传文件并设置文件URL
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String path = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);
            updateReqVO.setFileUrl(CarbonFileMinioUploadVO.of(minioProperties, fileName, path).getUrl());
        } else if (StrUtil.isBlank(updateReqVO.getFileUrl()) && updateReqVO.getId() != null) {
            // 未上传新文件且fileUrl为空时，保留原有的文件URL
            CarbonDocumentRespVO existingDoc = documentService.getDocument(updateReqVO.getId());
            if (existingDoc != null) {
                updateReqVO.setFileUrl(existingDoc.getFileUrl());
            }
        }
        documentService.updateDocument(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:document:delete')")
    public CommonResult<Boolean> deleteDocument(@RequestParam("id") Long id) {
        documentService.deleteDocument(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文档")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:document:query')")
    public CommonResult<CarbonDocumentRespVO> getDocument(@RequestParam("id") Long id) {
        return CommonResult.success(documentService.getDocument(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文档分页")
    @PreAuthorize("@ss.hasPermission('carbon:document:query')")
    public CommonResult<PageResult<CarbonDocumentRespVO>> getDocumentPage(@Valid CarbonDocumentPageReqVO pageReqVO) {
        return CommonResult.success(documentService.getDocumentPage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得碳核算标准库列表")
    @PreAuthorize("@ss.hasPermission('carbon:document:query')")
    public CommonResult<List<CarbonDocumentRespVO>> getDocumentList(@Valid CarbonDocumentReqVO reqVO) {
        return CommonResult.success(documentService.getDocumentList(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出文档管理 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:document:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportDocumentExcel(@Valid CarbonDocumentPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonDocumentRespVO> result;
        if (CollUtil.isNotEmpty(pageReqVO.getIds())) {
            // 选中导出：根据ID列表查询
            result = documentService.getDocumentListByIds(pageReqVO.getIds());
        } else {
            // 全部导出 或 筛选导出：根据条件查询
            CarbonDocumentReqVO reqVO = CarbonDocumentConvert.INSTANCE.convert(pageReqVO);
            result = documentService.getDocumentList(reqVO);
        }
        List<CarbonDocumentExcelVO> list = CarbonDocumentConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "文档管理.xls", "数据", CarbonDocumentExcelVO.class, list);
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文档文件")
    public CommonResult<CarbonFileMinioUploadVO> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String path  = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);
        CarbonFileMinioUploadVO uploadFile = CarbonFileMinioUploadVO.of(minioProperties, fileName, path);
        return CommonResult.success(uploadFile);
    }
}
