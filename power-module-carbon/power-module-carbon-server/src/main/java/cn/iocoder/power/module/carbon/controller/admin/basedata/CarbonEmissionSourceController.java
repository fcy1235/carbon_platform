package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonEmissionSourceConvert;

import cn.iocoder.power.module.carbon.service.CarbonEmissionSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 排放源")
@RestController
@RequestMapping("/carbon/emission-source")
@Validated
public class CarbonEmissionSourceController {

    @Resource
    private CarbonEmissionSourceService emissionSourceService;

    @PostMapping("/create")
    @Operation(summary = "创建排放源")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:create')")
    public CommonResult<Long> createEmissionSource(@Valid @RequestBody CarbonEmissionSourceSaveReqVO createReqVO) {
        Long id = emissionSourceService.createEmissionSource(createReqVO);
        return CommonResult.success(id);
    }

    @GetMapping("/getCode")
    @Operation(summary = "获取编码")
    public CommonResult<String> generateSourceCode() {
       String emissionSourceCode = emissionSourceService.generateSourceCode();
       return CommonResult.success(emissionSourceCode);
    }

    @PutMapping("/update")
    @Operation(summary = "更新排放源")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:update')")
    public CommonResult<Boolean> updateEmissionSource(@Valid @RequestBody CarbonEmissionSourceSaveReqVO updateReqVO) {
        emissionSourceService.updateEmissionSource(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排放源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:delete')")
    public CommonResult<Boolean> deleteEmissionSource(@RequestParam("id") Long id) {
        emissionSourceService.deleteEmissionSource(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排放源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:query')")
    public CommonResult<CarbonEmissionSourceRespVO> getEmissionSource(@RequestParam("id") Long id) {
        return CommonResult.success(emissionSourceService.getEmissionSource(id));
    }

    @GetMapping("/getList")
    @Operation(summary = "获得排放源")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:query')")
    public CommonResult<List<CarbonEmissionSourceRespVO>> getEmissionSourceList(@Valid CarbonEmissionSourceReqVO reqVO) {
        return CommonResult.success(emissionSourceService.getEmissionSourceList(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排放源分页")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:query')")
    public CommonResult<PageResult<CarbonEmissionSourceRespVO>> getEmissionSourcePage(@Valid CarbonEmissionSourcePageReqVO pageReqVO) {
        return CommonResult.success(emissionSourceService.getEmissionSourcePage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排放源 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:emission-source:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportEmissionSourceExcel(@Valid CarbonEmissionSourcePageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonEmissionSourceRespVO> result = emissionSourceService.getEmissionSourceListByPage(pageReqVO);
        List<CarbonEmissionSourceExcelVO> list = CarbonEmissionSourceConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "排放源.xls", "数据", CarbonEmissionSourceExcelVO.class, list);
    }
}
