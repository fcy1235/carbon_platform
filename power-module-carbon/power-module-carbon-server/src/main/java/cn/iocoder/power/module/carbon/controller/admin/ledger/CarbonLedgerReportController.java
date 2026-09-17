package cn.iocoder.power.module.carbon.controller.admin.ledger;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.*;
import cn.iocoder.power.module.carbon.service.CarbonLedgerReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Tag(name = "管理后台 - 台账上报")
@RestController
@RequestMapping("/carbon/ledger-report")
@Validated
public class CarbonLedgerReportController {

    @Resource
    private CarbonLedgerReportService ledgerReportService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "创建台账上报")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-report:create')")
    public CommonResult<Long> createLedgerReport(
            @Valid @RequestPart("reportData") CarbonLedgerReportSaveReqVO createReqVO,
            @RequestPart(value = "stampedReportFile", required = false) MultipartFile stampedReportFile) throws Exception {
        Long id = ledgerReportService.createLedgerReport(createReqVO, stampedReportFile);
        return CommonResult.success(id);
    }

    @PutMapping(value = "/update", consumes = "multipart/form-data")
    @Operation(summary = "更新台账上报")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-report:update')")
    public CommonResult<Boolean> updateLedgerReport(
            @Valid @RequestPart("reportData") CarbonLedgerReportSaveReqVO updateReqVO,
            @RequestPart(value = "stampedReportFile", required = false) MultipartFile stampedReportFile) throws Exception {
        ledgerReportService.updateLedgerReport(updateReqVO, stampedReportFile);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除台账上报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-report:delete')")
    public CommonResult<Boolean> deleteLedgerReport(@RequestParam("id") Long id) {
        ledgerReportService.deleteLedgerReport(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得台账上报详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-report:query')")
    @DataPermission
    public CommonResult<CarbonLedgerReportRespVO> getLedgerReport(@RequestParam("id") Long id) {
        return CommonResult.success(ledgerReportService.getLedgerReport(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得台账上报分页")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-report:query')")
    @DataPermission
    public CommonResult<PageResult<CarbonLedgerReportRespVO>> getLedgerReportPage(@Valid CarbonLedgerReportPageReqVO pageReqVO) {
        return CommonResult.success(ledgerReportService.getLedgerReportPage(pageReqVO));
    }

    @PutMapping("/submit")
    @Operation(summary = "提交台账上报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:ledger-report:submit')")
    public CommonResult<Boolean> submitLedgerReport(@RequestParam("id") Long id) {
        ledgerReportService.submitLedgerReport(id);
        return CommonResult.success(true);
    }


}
