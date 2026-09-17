package cn.iocoder.power.module.system.controller.admin.area;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.system.controller.admin.area.vo.*;
import cn.iocoder.power.module.system.service.area.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.power.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 行政区")
@RestController
@RequestMapping("/system/area")
@Validated
public class AreaController {

    @Resource
    private AreaService areaService;

    @PostMapping("/create")
    @Operation(summary = "创建行政区")
    @PreAuthorize("@ss.hasPermission('system:area:create')")
    public CommonResult<Long> createArea(@Valid @RequestBody AreaSaveReqVO createReqVO) {
        Long id = areaService.createArea(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "修改行政区")
    @PreAuthorize("@ss.hasPermission('system:area:update')")
    public CommonResult<Boolean> updateArea(@Valid @RequestBody AreaSaveReqVO updateReqVO) {
        areaService.updateArea(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除行政区")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:area:delete')")
    public CommonResult<Boolean> deleteArea(@RequestParam("id") Long id) {
        areaService.deleteArea(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除行政区")
    @Parameter(name = "ids", description = "编号数组", required = true)
    @PreAuthorize("@ss.hasPermission('system:area:delete')")
    public CommonResult<Boolean> deleteAreaList(@RequestParam("ids") List<Long> ids) {
        areaService.deleteAreaList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得行政区详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:area:query')")
    public CommonResult<AreaRespVO> getArea(@RequestParam("id") Long id) {
        return success(areaService.getArea(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得行政区列表")
    @PreAuthorize("@ss.hasPermission('system:area:query')")
    public CommonResult<List<AreaRespVO>> getAreaList(AreaListReqVO reqVO) {
        return success(areaService.getAreaList(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得行政区分页")
    @PreAuthorize("@ss.hasPermission('system:area:query')")
    public CommonResult<PageResult<AreaRespVO>> getAreaPage(@Validated AreaPageReqVO pageReqVO) {
        return success(areaService.getAreaPage(pageReqVO));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得行政区精简列表", description = "用于下拉选择")
    public CommonResult<List<AreaSimpleRespVO>> getAreaSimpleList(AreaListReqVO reqVO) {
        return success(areaService.getAreaSimpleList(reqVO));
    }

    @GetMapping("/tree")
    @Operation(summary = "获得行政区树")
    @PreAuthorize("@ss.hasPermission('system:area:query')")
    public CommonResult<List<AreaTreeRespVO>> getAreaTree(AreaListReqVO reqVO) {
        return success(areaService.getAreaTree(reqVO));
    }

}
