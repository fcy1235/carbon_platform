package cn.iocoder.power.module.carbon.controller.admin.maintenance;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonMaintenanceStationConvert;
import cn.iocoder.power.module.carbon.service.CarbonMaintenanceStationService;
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

@Tag(name = "管理后台 - 维保网点")
@RestController
@RequestMapping("/carbon/maintenance-station")
@Validated
public class CarbonMaintenanceStationController {

    @Resource
    private CarbonMaintenanceStationService stationService;

    @PostMapping("/create")
    @Operation(summary = "创建维保网点")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:create')")
    public CommonResult<Long> createStation(@Valid @RequestBody CarbonMaintenanceStationSaveReqVO createReqVO) {
        Long id = stationService.createStation(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新维保网点")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:update')")
    public CommonResult<Boolean> updateStation(@Valid @RequestBody CarbonMaintenanceStationSaveReqVO updateReqVO) {
        stationService.updateStation(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除维保网点")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:delete')")
    public CommonResult<Boolean> deleteStation(@RequestParam("id") Long id) {
        stationService.deleteStation(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得维保网点")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:query')")
    public CommonResult<CarbonMaintenanceStationRespVO> getStation(@RequestParam("id") Long id) {
        return CommonResult.success(stationService.getStation(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得维保网点分页")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:query')")
    public CommonResult<PageResult<CarbonMaintenanceStationRespVO>> getStationPage(@Valid CarbonMaintenanceStationPageReqVO pageReqVO) {
        return CommonResult.success(stationService.getStationPage(pageReqVO));
    }

    @GetMapping("/list-by-enterprise")
    @Operation(summary = "获得指定企业的网点列表")
    @Parameter(name = "enterpriseId", description = "企业编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:query')")
    public CommonResult<List<CarbonMaintenanceStationRespVO>> getStationListByEnterpriseId(@RequestParam("enterpriseId") Long enterpriseId) {
        return CommonResult.success(stationService.getStationListByEnterpriseId(enterpriseId));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出维保网点 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:maintenance-station:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportStationExcel(@Valid CarbonMaintenanceStationPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        List<CarbonMaintenanceStationRespVO> result = stationService.getStationList(pageReqVO);
        List<CarbonMaintenanceStationExcelVO> list = CarbonMaintenanceStationConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "维保网点.xls", "数据", CarbonMaintenanceStationExcelVO.class, list);
    }
}
