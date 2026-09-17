package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonFactorLibConvert;

import cn.iocoder.power.module.carbon.service.CarbonFactorLibService;
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

@Tag(name = "管理后台 - 排放因子库")
@RestController
@RequestMapping("/carbon/factor-lib")
@Validated
public class CarbonFactorLibController {

    @Resource
    private CarbonFactorLibService factorLibService;

    @PostMapping("/create")
    @Operation(summary = "创建排放因子")
    @PreAuthorize("@ss.hasPermission('carbon:factor-lib:create')")
    public CommonResult<Long> createFactorLib(@Valid @RequestBody CarbonFactorLibSaveReqVO createReqVO) {
        Long id = factorLibService.createFactorLib(createReqVO);
        return CommonResult.success(id);
    }

    @GetMapping("/getCode")
    @Operation(summary = "获取编码")
    public CommonResult<String> generateCode() {
        String emissionSourceCode = factorLibService.generateFactorCode();
        return CommonResult.success(emissionSourceCode);
    }

    @PostMapping("/update")
    @Operation(summary = "更新排放因子")
    @PreAuthorize("@ss.hasPermission('carbon:factor-lib:update')")
    public CommonResult<Boolean> updateFactorLib(@Valid @RequestBody CarbonFactorLibSaveReqVO updateReqVO) {
        factorLibService.updateFactorLib(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排放因子")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:factor-lib:delete')")
    public CommonResult<Boolean> deleteFactorLib(@RequestParam("id") Long id) {
        factorLibService.deleteFactorLib(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排放因子")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:factor-lib:query')")
    public CommonResult<CarbonFactorLibRespVO> getFactorLib(@RequestParam("id") Long id) {
        return CommonResult.success(factorLibService.getFactorLib(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排放因子分页")
    @PreAuthorize("@ss.hasPermission('carbon:factor-lib:query')")
    public CommonResult<PageResult<CarbonFactorLibRespVO>> getFactorLibPage(@Valid CarbonFactorLibPageReqVO pageReqVO) {
        return CommonResult.success(factorLibService.getFactorLibPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排放因子 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:factor-lib:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportFactorLibExcel(@Valid CarbonFactorLibPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        List<CarbonFactorLibRespVO> result;
        if (CollUtil.isNotEmpty(pageReqVO.getIds())) {
            // 选中导出：根据ID列表查询
            result = factorLibService.getFactorLibListByIds(pageReqVO.getIds());
        } else {
            // 全部导出 或 筛选导出：根据条件查询
            CarbonFactorLibReqVO reqVO = CarbonFactorLibConvert.INSTANCE.convert(pageReqVO);
            result = factorLibService.getFactorLibList(reqVO);
        }
        List<CarbonFactorLibExcelVO> list = CarbonFactorLibConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "排放因子库.xlsx", "数据", CarbonFactorLibExcelVO.class, list);
    }


}
