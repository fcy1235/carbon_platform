package cn.iocoder.power.module.carbon.controller.admin.maintenance;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonGasCoordinatorConvert;
import cn.iocoder.power.module.carbon.service.CarbonGasCoordinatorService;
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

@Tag(name = "管理后台 - 农村气代煤协管员")
@RestController
@RequestMapping("/carbon/gas-coordinator")
@Validated
public class CarbonGasCoordinatorController {

    @Resource
    private CarbonGasCoordinatorService coordinatorService;

    @PostMapping("/create")
    @Operation(summary = "创建农村气代煤协管员")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:create')")
    public CommonResult<Long> createCoordinator(@Valid @RequestBody CarbonGasCoordinatorSaveReqVO createReqVO) {
        Long id = coordinatorService.createCoordinator(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新农村气代煤协管员")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:update')")
    public CommonResult<Boolean> updateCoordinator(@Valid @RequestBody CarbonGasCoordinatorSaveReqVO updateReqVO) {
        coordinatorService.updateCoordinator(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除农村气代煤协管员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:delete')")
    public CommonResult<Boolean> deleteCoordinator(@RequestParam("id") Long id) {
        coordinatorService.deleteCoordinator(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得农村气代煤协管员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:query')")
    public CommonResult<CarbonGasCoordinatorRespVO> getCoordinator(@RequestParam("id") Long id) {
        return CommonResult.success(coordinatorService.getCoordinator(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得农村气代煤协管员分页")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:query')")
    public CommonResult<PageResult<CarbonGasCoordinatorRespVO>> getCoordinatorPage(@Valid CarbonGasCoordinatorPageReqVO pageReqVO) {
        return CommonResult.success(coordinatorService.getCoordinatorPage(pageReqVO));
    }

    @GetMapping("/list-by-enterprise")
    @Operation(summary = "获得指定企业的协管员列表")
    @Parameter(name = "enterpriseId", description = "企业编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:query')")
    public CommonResult<List<CarbonGasCoordinatorRespVO>> getCoordinatorListByEnterpriseId(@RequestParam("enterpriseId") Long enterpriseId) {
        return CommonResult.success(coordinatorService.getCoordinatorListByEnterpriseId(enterpriseId));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出农村气代煤协管员 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:gas-coordinator:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportCoordinatorExcel(@Valid CarbonGasCoordinatorPageReqVO pageReqVO,
                                       HttpServletResponse response) throws IOException {
        List<CarbonGasCoordinatorRespVO> result = coordinatorService.getCoordinatorList(pageReqVO);
        List<CarbonGasCoordinatorExcelVO> list = CarbonGasCoordinatorConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "农村气代煤协管员.xls", "数据", CarbonGasCoordinatorExcelVO.class, list);
    }
}
