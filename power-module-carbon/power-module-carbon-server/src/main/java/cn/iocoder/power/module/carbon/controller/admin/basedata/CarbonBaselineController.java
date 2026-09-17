package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonBaselineConvert;
import cn.iocoder.power.module.carbon.service.CarbonBaselineService;
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
import java.util.Collections;
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

@Tag(name = "管理后台 - 基准线碳排放强度")
@RestController
@RequestMapping("/carbon/baseline")
@Validated
public class CarbonBaselineController {

    @Resource
    private CarbonBaselineService baselineService;

    @PostMapping("/create")
    @Operation(summary = "创建基准线")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:create')")
    public CommonResult<Long> createBaseline(@Valid @RequestBody CarbonBaselineSaveReqVO createReqVO) {
        Long id = baselineService.createBaseline(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新基准线")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:update')")
    public CommonResult<Boolean> updateBaseline(@Valid @RequestBody CarbonBaselineSaveReqVO updateReqVO) {
        baselineService.updateBaseline(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除基准线")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:delete')")
    public CommonResult<Boolean> deleteBaseline(@RequestParam("id") Long id) {
        baselineService.deleteBaseline(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得基准线")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:query')")
    public CommonResult<CarbonBaselineRespVO> getBaseline(@RequestParam("id") Long id) {
        return CommonResult.success(baselineService.getBaseline(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得基准线分页")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:query')")
    public CommonResult<PageResult<CarbonBaselineRespVO>> getBaselinePage(@Valid CarbonBaselinePageReqVO pageReqVO) {
        return CommonResult.success(baselineService.getBaselinePage(pageReqVO));
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "根据行政区编码获得单个基准线")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:query')")
    public CommonResult<CarbonBaselineRespVO> getBaselineByCode(
            @RequestParam(value = "provinceCode", required = false) Long provinceCode,
            @RequestParam(value = "cityCode", required = false) Long cityCode,
            @RequestParam(value = "districtCode", required = false) Long districtCode) {
        // 三选一：districtCode > cityCode > provinceCode，复用 selectList 互斥查询逻辑
        CarbonBaselinePageReqVO reqVO = new CarbonBaselinePageReqVO();
        if (districtCode != null) {
            reqVO.setDistrictCodes(Collections.singletonList(districtCode));
        } else if (cityCode != null) {
            reqVO.setCityCodes(Collections.singletonList(cityCode));
        } else if (provinceCode != null) {
            reqVO.setProvinceCodes(Collections.singletonList(provinceCode));
        } else {
            return CommonResult.error(400, "行政区编码不能为空");
        }
        List<CarbonBaselineRespVO> list = baselineService.getBaselineList(reqVO);
        return CommonResult.success(CollUtil.isNotEmpty(list) ? list.get(0) : null);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出基准线Excel")
    @PreAuthorize("@ss.hasPermission('carbon:baseline:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportBaselineExcel(@Valid CarbonBaselinePageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonBaselineRespVO> result = baselineService.getBaselineList(pageReqVO);
        List<CarbonBaselineExcelVO> list = CarbonBaselineConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "基准线碳排放强度.xls", "数据", CarbonBaselineExcelVO.class, list);
    }
}
