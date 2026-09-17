package cn.iocoder.power.module.carbon.controller.admin.device;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonDeviceConvert;

import cn.iocoder.power.module.carbon.service.CarbonDeviceService;
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

@Tag(name = "管理后台 - 设备管理")
@RestController
@RequestMapping("/carbon/device")
@Validated
public class CarbonDeviceController {

    @Resource
    private CarbonDeviceService deviceService;

    @PostMapping("/create")
    @Operation(summary = "创建设备")
    @PreAuthorize("@ss.hasPermission('carbon:device:create')")
    public CommonResult<Long> createDevice(@Valid @RequestBody CarbonDeviceSaveReqVO createReqVO) {
        Long id = deviceService.createDevice(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备")
    @PreAuthorize("@ss.hasPermission('carbon:device:update')")
    public CommonResult<Boolean> updateDevice(@Valid @RequestBody CarbonDeviceSaveReqVO updateReqVO) {
        deviceService.updateDevice(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:device:delete')")
    public CommonResult<Boolean> deleteDevice(@RequestParam("id") Long id) {
        deviceService.deleteDevice(id);
        return CommonResult.success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "变更设备状态")
    @PreAuthorize("@ss.hasPermission('carbon:device:update')")
    public CommonResult<Boolean> updateDeviceStatus(@Valid @RequestBody CarbonDeviceStatusUpdateReqVO reqVO) {
        deviceService.updateDeviceStatus(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:device:query')")
    public CommonResult<CarbonDeviceRespVO> getDevice(@RequestParam("id") Long id) {
        return CommonResult.success(deviceService.getDevice(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备分页")
    @PreAuthorize("@ss.hasPermission('carbon:device:query')")
    public CommonResult<PageResult<CarbonDeviceRespVO>> getDevicePage(@Valid CarbonDevicePageReqVO pageReqVO) {
        return CommonResult.success(deviceService.getDevicePage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得设备简易列表")
    @PreAuthorize("@ss.hasPermission('carbon:device:query')")
    public CommonResult<List<CarbonDeviceSimpleRespVO>> getDeviceSimpleList(@Valid CarbonDevicePageReqVO pageReqVO) {
        return CommonResult.success(deviceService.getDeviceSimpleList(pageReqVO));
    }

    @GetMapping("/getCode")
    @Operation(summary = "获取编码")
    public CommonResult<String> generateCode(@RequestParam("prefix") String prefix) {
        // 编码规则：（设备类型前缀）-6位序号，如 GM-000001
        return CommonResult.success(deviceService.generateCode(prefix));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:device:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportDeviceExcel(@Valid CarbonDevicePageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonDeviceRespVO> result = deviceService.getDeviceList(pageReqVO);
        List<CarbonDeviceExcelVO> list = CarbonDeviceConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "设备管理.xls", "数据", CarbonDeviceExcelVO.class, list);
    }
}
