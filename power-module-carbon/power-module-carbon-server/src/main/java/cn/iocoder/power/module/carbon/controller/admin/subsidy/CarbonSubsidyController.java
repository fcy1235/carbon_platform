package cn.iocoder.power.module.carbon.controller.admin.subsidy;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonSubsidyConvert;

import cn.iocoder.power.module.carbon.service.CarbonSubsidyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;

@Tag(name = "管理后台 - 补贴管理")
@RestController
@RequestMapping("/carbon/subsidy")
@Validated
public class CarbonSubsidyController {

    @Resource
    private CarbonSubsidyService subsidyService;

    @PostMapping("/create")
    @Operation(summary = "创建补贴")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:create')")
    public CommonResult<Long> createSubsidy(@Valid @RequestBody CarbonSubsidySaveReqVO createReqVO) {
        Long id = subsidyService.createSubsidy(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新补贴")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:update')")
    public CommonResult<Boolean> updateSubsidy(@Valid @RequestBody CarbonSubsidySaveReqVO updateReqVO) {
        subsidyService.updateSubsidy(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除补贴")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:delete')")
    public CommonResult<Boolean> deleteSubsidy(@RequestParam("id") Long id) {
        subsidyService.deleteSubsidy(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得补贴")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:query')")
    public CommonResult<CarbonSubsidyRespVO> getSubsidy(@RequestParam("id") Long id) {
        return CommonResult.success(subsidyService.getSubsidy(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得补贴分页")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:query')")
    public CommonResult<PageResult<CarbonSubsidyRespVO>> getSubsidyPage(@Valid CarbonSubsidyPageReqVO pageReqVO) {
        return CommonResult.success(subsidyService.getSubsidyPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出补贴 Excel（支持全部导出/筛选导出/选中导出）")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportSubsidyExcel(@Valid CarbonSubsidyPageReqVO reqVO, HttpServletResponse response) throws IOException {
        List<CarbonSubsidyRespVO> result = subsidyService.getSubsidyList(reqVO);
        List<CarbonSubsidyExcelVO> list = CarbonSubsidyConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "补贴管理.xls", "数据", CarbonSubsidyExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/补贴导入模板.xlsx");
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
    @Operation(summary = "导入数据")
    @PreAuthorize("@ss.hasPermission('carbon:subsidy:import')")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<CarbonSubsidyImportVO> list = ExcelUtils.read(file, CarbonSubsidyImportVO.class);
        subsidyService.importSubsidyList(list);
        return CommonResult.success("导入成功");
    }
}
