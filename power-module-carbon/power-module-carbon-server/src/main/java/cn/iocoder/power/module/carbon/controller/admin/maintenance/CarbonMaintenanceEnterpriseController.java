package cn.iocoder.power.module.carbon.controller.admin.maintenance;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonMaintenanceEnterpriseConvert;
import cn.iocoder.power.module.carbon.service.CarbonMaintenanceEnterpriseService;
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

@Tag(name = "管理后台 - 维保企业")
@RestController
@RequestMapping("/carbon/maintenance-enterprise")
@Validated
public class CarbonMaintenanceEnterpriseController {

    @Resource
    private CarbonMaintenanceEnterpriseService enterpriseService;

    @PostMapping("/create")
    @Operation(summary = "创建维保企业")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-enterprise:create')")
    public CommonResult<Long> createEnterprise(@Valid @RequestBody CarbonMaintenanceEnterpriseSaveReqVO createReqVO) {
        Long id = enterpriseService.createEnterprise(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新维保企业")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-enterprise:update')")
    public CommonResult<Boolean> updateEnterprise(@Valid @RequestBody CarbonMaintenanceEnterpriseSaveReqVO updateReqVO) {
        enterpriseService.updateEnterprise(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除维保企业")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-enterprise:delete')")
    public CommonResult<Boolean> deleteEnterprise(@RequestParam("id") Long id) {
        enterpriseService.deleteEnterprise(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得维保企业")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-enterprise:query')")
    public CommonResult<CarbonMaintenanceEnterpriseRespVO> getEnterprise(@RequestParam("id") Long id) {
        return CommonResult.success(enterpriseService.getEnterprise(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得维保企业分页")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-enterprise:query')")
    public CommonResult<PageResult<CarbonMaintenanceEnterpriseRespVO>> getEnterprisePage(@Valid CarbonMaintenanceEnterprisePageReqVO pageReqVO) {
        return CommonResult.success(enterpriseService.getEnterprisePage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出维保企业 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-enterprise:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportEnterpriseExcel(@Valid CarbonMaintenanceEnterprisePageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        List<CarbonMaintenanceEnterpriseRespVO> result = enterpriseService.getEnterpriseList(pageReqVO);
        List<CarbonMaintenanceEnterpriseExcelVO> list = CarbonMaintenanceEnterpriseConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "维保企业.xls", "数据", CarbonMaintenanceEnterpriseExcelVO.class, list);
    }
}
