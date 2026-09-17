package cn.iocoder.power.module.carbon.controller.admin.device;

import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceDataSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonDeviceDataConvert;
import cn.iocoder.power.module.carbon.service.CarbonDeviceDataService;
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

@Tag(name = "管理后台 - 设备数据")
@RestController
@RequestMapping("/carbon/device-data")
@Validated
public class CarbonDeviceDataController {

    @Resource
    private CarbonDeviceDataService deviceDataService;

    @PostMapping("/create")
    @Operation(summary = "新增设备数据（外部接口上报写入）")
    @PreAuthorize("@ss.hasPermission('carbon:device-data:create')")
    public CommonResult<Long> createDeviceData(@Valid @RequestBody CarbonDeviceDataSaveReqVO createReqVO) {
        return CommonResult.success(deviceDataService.createDeviceData(createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:device-data:query')")
    public CommonResult<CarbonDeviceDataRespVO> getDeviceData(@RequestParam("id") Long id) {
        return CommonResult.success(deviceDataService.getDeviceData(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备数据分页（每设备最新一条）")
    @PreAuthorize("@ss.hasPermission('carbon:device-data:query')")
    public CommonResult<PageResult<CarbonDeviceDataRespVO>> getDeviceDataPage(@Valid CarbonDeviceDataPageReqVO pageReqVO) {
        return CommonResult.success(deviceDataService.getDeviceDataPage(pageReqVO));
    }

    @GetMapping("/detail")
    @Operation(summary = "获得设备数据详情（该设备所有上报记录）")
    @Parameter(name = "deviceCode", description = "设备编码", required = true, example = "ASHP-000001")
    @PreAuthorize("@ss.hasPermission('carbon:device-data:query')")
    public CommonResult<PageResult<CarbonDeviceDataRespVO>> getDeviceDataDetail(@RequestParam("deviceCode") String deviceCode) {
        return CommonResult.success(deviceDataService.getDeviceDataDetail(deviceCode));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备数据（支持单条和批量）")
    @Parameter(name = "ids", description = "数据ID集合", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('carbon:device-data:delete')")
    public CommonResult<Boolean> deleteDeviceData(@RequestParam("ids") List<Long> ids) {
        deviceDataService.deleteDeviceData(ids);
        return CommonResult.success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出设备数据 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:device-data:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportDeviceDataExcel(@Valid CarbonDeviceDataPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonDeviceDataRespVO> result = deviceDataService.getDeviceDataList(pageReqVO);
        List<CarbonDeviceDataExcelVO> list = CarbonDeviceDataConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "设备数据.xls", "数据", CarbonDeviceDataExcelVO.class, list);
    }
}
