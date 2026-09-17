package cn.iocoder.power.module.carbon.controller.admin.accounting;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonAccountingConvert;
import cn.iocoder.power.module.carbon.service.CarbonAccountingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;


import java.io.IOException;
import java.util.List;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;



@Tag(name = "管理后台 - 碳排放核算")
@RestController
@RequestMapping("/carbon/accounting")
@Validated
public class CarbonAccountingController {

    @Resource
    private CarbonAccountingService accountingService;

    @PostMapping("/create")
    @Operation(summary = "创建碳排放核算")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:create')")
    public CommonResult<Long> createAccounting(@Valid @RequestBody CarbonAccountingSaveReqVO createReqVO) {
        Long id = accountingService.createAccounting(createReqVO);
        return CommonResult.success(id);
    }

    @PostMapping("/create-batch")
    @Operation(summary = "批量创建碳排放核算")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:create')")
    public CommonResult<List<Long>> createAccountingBatch(@Valid @RequestBody List<CarbonAccountingSaveReqVO> createReqVOList) {
        List<Long> ids = accountingService.createAccountingBatch(createReqVOList);
        return CommonResult.success(ids);
    }



    @PutMapping("/update")
    @Operation(summary = "更新碳排放核算")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:update')")
    public CommonResult<Boolean> updateAccounting(@Valid @RequestBody CarbonAccountingSaveReqVO updateReqVO) {
        accountingService.updateAccounting(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除碳排放核算")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:delete')")
    public CommonResult<Boolean> deleteAccounting(@RequestParam("id") Long id) {
        accountingService.deleteAccounting(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得碳排放核算")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:query')")
    public CommonResult<CarbonAccountingRespVO> getAccounting(@RequestParam("id") Long id) {
        return CommonResult.success(accountingService.getAccounting(id));
    }

    @GetMapping("/getAccountUserInfoPage")
    @Operation(summary = "获得碳核算用户基本信息分页")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:query')")
    public CommonResult<PageResult<CarbonAccountUserInfoRespVO>> getAccountUserInfoPage(@Valid CarbonAccountingPageReqVO pageReqVO) {
        return CommonResult.success(accountingService.getAccountUserInfoPage(pageReqVO));
    }

    @GetMapping("/getUnaccountedUserPage")
    @Operation(summary = "获得未核算用户分页（项目管理选择用户，用户表为主表）")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:query')")
    public CommonResult<PageResult<CarbonAccountUserInfoRespVO>> getUnaccountedUserPage(@Valid CarbonAccountingPageReqVO pageReqVO) {
        return CommonResult.success(accountingService.getUnaccountedUserPage(pageReqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得碳排放核算分页（项目管理选择用户）")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:query')")
    public CommonResult<PageResult<CarbonAccountUserInfoRespVO>> getAccountingPage(@Valid CarbonAccountingPageReqVO pageReqVO) {
        return CommonResult.success(accountingService.getAccountingPage(pageReqVO));
    }

    @PostMapping("/calculate")
    @Operation(summary = "试算减排量")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:query')")
    public CommonResult<CarbonAccountingRespVO> calculateReduction(@Valid @RequestBody CarbonAccountingCalculateReqVO reqVO) {
        CarbonAccountingRespVO result = accountingService.calculateReduction(reqVO);
        return CommonResult.success(result);
    }

    @GetMapping("/init-data")
    @Operation(summary = "获取核算初始化数据（选择用户后一次性返回基准线、用量、排放因子、减排量、设备列表）")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:query')")
    public CommonResult<List<CarbonAccountingInitRespVO>> getInitData(@Valid CarbonAccountingInitReqVO reqVO) {
        return CommonResult.success(accountingService.getInitData(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出碳排放核算Excel")
    @PreAuthorize("@ss.hasPermission('carbon:accounting:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportAccountingExcel(@Valid CarbonAccountingPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonAccountingRespVO> result = accountingService.getAccountingList(pageReqVO);
        List<CarbonAccountingExcelVO> list = CarbonAccountingConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "碳排放核算.xls", "数据", CarbonAccountingExcelVO.class, list);
    }
}
