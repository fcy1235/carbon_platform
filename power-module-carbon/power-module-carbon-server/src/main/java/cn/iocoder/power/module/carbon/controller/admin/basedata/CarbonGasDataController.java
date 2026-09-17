package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonGasDataConvert;

import cn.iocoder.power.module.carbon.service.CarbonGasDataService;
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
import java.util.Collection;
import java.util.List;

import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;

@Tag(name = "管理后台 - 燃气数据")
@RestController
@RequestMapping("/carbon/gas-data")
@Validated
public class CarbonGasDataController {

    @Resource
    private CarbonGasDataService gasDataService;

    @GetMapping("/get")
    @Operation(summary = "获得燃气数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:query')")
    public CommonResult<CarbonGasDataRespVO> getGasData(@RequestParam("id") Long id) {
        return CommonResult.success(gasDataService.getGasData(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得燃气数据分页（每户最新一条）")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:query')")
    public CommonResult<PageResult<CarbonGasDataRespVO>> getGasDataPage(@Valid CarbonGasDataPageReqVO pageReqVO) {
        return CommonResult.success(gasDataService.getGasDataPage(pageReqVO));
    }

    @GetMapping("/detail")
    @Operation(summary = "获得燃气数据详情（该户所有记录）")
    @Parameter(name = "gasId", description = "燃气户号", required = true, example = "G20000")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:query')")
    public CommonResult<PageResult<CarbonGasDataRespVO>> getGasDataDetail(@RequestParam("gasId") String gasId) {
        return CommonResult.success(gasDataService.getGasDataDetail(gasId));
    }

    @PostMapping("/sync")
    @Operation(summary = "同步单条燃气数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:update')")
    public CommonResult<Boolean> syncGasData(@RequestParam("id") Long id) {
        gasDataService.syncGasData(id);
        return CommonResult.success(true);
    }

    @PostMapping("/sync-all")
    @Operation(summary = "同步全部燃气数据")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:update')")
    public CommonResult<Boolean> syncAllGasData() {
        gasDataService.syncAllGasData();
        return CommonResult.success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出燃气数据 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportGasDataExcel(@Valid CarbonGasDataPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonGasDataRespVO> result = gasDataService.getGasDataList(pageReqVO);
        List<CarbonGasDataExcelVO> list = CarbonGasDataConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "燃气数据.xls", "数据", CarbonGasDataExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/燃气数据导入模板.xlsx");
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
    @Operation(summary = "导入燃气数据")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:import')")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<CarbonGasDataImportVO> list = ExcelUtils.read(file, CarbonGasDataImportVO.class);
        gasDataService.importGasDataList(list);
        return CommonResult.success("导入成功");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除燃气数据（支持单条和批量）")
    @Parameter(name = "ids", description = "数据ID集合", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:delete')")
    public CommonResult<Boolean> deleteGasData(@RequestParam("ids") List<Long> ids) {
        gasDataService.deleteGasData(ids);
        return CommonResult.success(true);
    }

    @DeleteMapping("/deleteByGasId")
    @Operation(summary = "删除燃气数据（支持单条和批量）")
    @Parameter(name = "ids", description = "数据ID集合", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('carbon:gas-data:delete')")
    public CommonResult<Boolean> deleteByGasId(@RequestParam("ids") Collection<String> gasIds) {
        gasDataService.deleteGasDataByGasId(gasIds);
        return CommonResult.success(true);
    }
}
