package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonElectricityConvert;

import cn.iocoder.power.module.carbon.service.CarbonElectricityService;
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

@Tag(name = "管理后台 - 电力数据")
@RestController
@RequestMapping("/carbon/electricity")
@Validated
public class CarbonElectricityController {

    @Resource
    private CarbonElectricityService electricityService;

    @GetMapping("/get")
    @Operation(summary = "获得电力数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:query')")
    public CommonResult<CarbonElectricityRespVO> getElectricity(@RequestParam("id") Long id) {
        return CommonResult.success(electricityService.getElectricity(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得电力数据分页（每户最新一条）")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:query')")
    public CommonResult<PageResult<CarbonElectricityRespVO>> getElectricityPage(@Valid CarbonElectricityPageReqVO pageReqVO) {
        return CommonResult.success(electricityService.getElectricityPage(pageReqVO));
    }

    @GetMapping("/detail")
    @Operation(summary = "获得电力数据详情（该户所有记录）")
    @Parameter(name = "electricityId", description = "电力户号", required = true, example = "E10000")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:query')")
    public CommonResult<PageResult<CarbonElectricityRespVO>> getElectricityDetail(@RequestParam("electricityId") String electricityId) {
        return CommonResult.success(electricityService.getElectricityDetail(electricityId));
    }

    @PostMapping("/sync")
    @Operation(summary = "同步单条电力数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:update')")
    public CommonResult<Boolean> syncElectricity(@RequestParam("id") Long id) {
        electricityService.syncElectricity(id);
        return CommonResult.success(true);
    }

    @PostMapping("/sync-all")
    @Operation(summary = "同步全部电力数据")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:update')")
    public CommonResult<Boolean> syncAllElectricity() {
        electricityService.syncAllElectricity();
        return CommonResult.success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出电力数据 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportElectricityExcel(@Valid CarbonElectricityPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonElectricityRespVO> result = electricityService.getElectricityList(pageReqVO);
        List<CarbonElectricityExcelVO> list = CarbonElectricityConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "电力数据.xls", "数据", CarbonElectricityExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/电力数据导入模板.xlsx");
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
    @Operation(summary = "导入电力数据")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:import')")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<CarbonElectricityImportVO> list = ExcelUtils.read(file, CarbonElectricityImportVO.class);
        electricityService.importElectricityList(list);
        return CommonResult.success("导入成功");
    }

    @DeleteMapping("/deleteByElectricIds")
    @Operation(summary = "根据电力号删除电力数据（支持单条和批量）")
    @Parameter(name = "ids", description = "数据ID集合", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:delete')")
    public CommonResult<Boolean> deleteElectricityDataByElectricIds(@RequestParam("electricIds") Collection<String> electricIds) {
        electricityService.deleteElectricityDataByElectricIds(electricIds);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "根据电力号删除电力数据（支持单条和批量）")
    @Parameter(name = "ids", description = "数据ID集合", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('carbon:electricity:delete')")
    public CommonResult<Boolean> deleteElectricityData(@RequestParam("ids") List<Long> ids) {
        electricityService.deleteElectricityData(ids);
        return CommonResult.success(true);
    }
}
