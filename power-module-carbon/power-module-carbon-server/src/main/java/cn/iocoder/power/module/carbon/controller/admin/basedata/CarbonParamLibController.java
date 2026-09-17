package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;

import cn.iocoder.power.module.carbon.service.CarbonParamLibService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 参数库")
@RestController
@RequestMapping("/carbon/param-lib")
@Validated
public class CarbonParamLibController {

    @Resource
    private CarbonParamLibService paramLibService;

    @PostMapping("/create")
    @Operation(summary = "创建参数")
    @PreAuthorize("@ss.hasPermission('carbon:param-lib:create')")
    public CommonResult<Long> createParamLib(@Valid @RequestBody CarbonParamLibSaveReqVO createReqVO) {
        Long id = paramLibService.createParamLib(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新参数")
    @PreAuthorize("@ss.hasPermission('carbon:param-lib:update')")
    public CommonResult<Boolean> updateParamLib(@Valid @RequestBody CarbonParamLibSaveReqVO updateReqVO) {
        paramLibService.updateParamLib(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除参数")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:param-lib:delete')")
    public CommonResult<Boolean> deleteParamLib(@RequestParam("id") Long id) {
        paramLibService.deleteParamLib(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得参数")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:param-lib:query')")
    public CommonResult<CarbonParamLibRespVO> getParamLib(@RequestParam("id") Long id) {
        return CommonResult.success(paramLibService.getParamLib(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得参数分页")
    @PreAuthorize("@ss.hasPermission('carbon:param-lib:query')")
    public CommonResult<PageResult<CarbonParamLibRespVO>> getParamLibPage(@Valid CarbonParamLibPageReqVO pageReqVO) {
        return CommonResult.success(paramLibService.getParamLibPage(pageReqVO));
    }
}
