package cn.iocoder.power.module.carbon.controller.admin.maintenance;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonGasSafetyOfficerConvert;
import cn.iocoder.power.module.carbon.service.CarbonGasSafetyOfficerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "管理后台 - 燃气安全员")
@RestController
@RequestMapping("/carbon/gas-safety-officer")
@Validated
public class CarbonGasSafetyOfficerController {

    @Resource
    private CarbonGasSafetyOfficerService officerService;

    @PostMapping("/create")
    @Operation(summary = "创建燃气安全员")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:create')")
    public CommonResult<Long> createOfficer(@Valid @RequestPart("data") CarbonGasSafetyOfficerSaveReqVO createReqVO,
                                           @RequestPart(value = "idCardFrontImage", required = false) MultipartFile idCardFrontImage) throws Exception {
        Long id = officerService.createOfficer(createReqVO, idCardFrontImage);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新燃气安全员")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:update')")
    public CommonResult<Boolean> updateOfficer(@Valid @RequestPart("data") CarbonGasSafetyOfficerSaveReqVO updateReqVO,
                                             @RequestPart(value = "idCardFrontImage", required = false) MultipartFile idCardFrontImage) throws Exception {
        officerService.updateOfficer(updateReqVO, idCardFrontImage);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除燃气安全员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:delete')")
    public CommonResult<Boolean> deleteOfficer(@RequestParam("id") Long id) {
        officerService.deleteOfficer(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得燃气安全员")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:query')")
    public CommonResult<CarbonGasSafetyOfficerRespVO> getOfficer(@RequestParam("id") Long id) {
        return CommonResult.success(officerService.getOfficer(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得燃气安全员分页")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:query')")
    public CommonResult<PageResult<CarbonGasSafetyOfficerRespVO>> getOfficerPage(@Valid CarbonGasSafetyOfficerPageReqVO pageReqVO) {
        return CommonResult.success(officerService.getOfficerPage(pageReqVO));
    }

    @GetMapping("/list-by-enterprise")
    @Operation(summary = "获得指定企业的安全员列表")
    @Parameter(name = "enterpriseId", description = "企业编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:query')")
    public CommonResult<List<CarbonGasSafetyOfficerRespVO>> getOfficerListByEnterpriseId(@RequestParam("enterpriseId") Long enterpriseId) {
        return CommonResult.success(officerService.getOfficerListByEnterpriseId(enterpriseId));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出燃气安全员 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:gas-safety-officer:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportOfficerExcel(@Valid CarbonGasSafetyOfficerPageReqVO pageReqVO,
                                   HttpServletResponse response) throws IOException {
        List<CarbonGasSafetyOfficerRespVO> result = officerService.getOfficerList(pageReqVO);
        List<CarbonGasSafetyOfficerExcelVO> list = CarbonGasSafetyOfficerConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "燃气安全员.xls", "数据", CarbonGasSafetyOfficerExcelVO.class, list);
    }
}
