package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonGasInfoConvert;

import cn.iocoder.power.module.carbon.service.CarbonGasInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "管理后台 - 气体信息")
@RestController
@RequestMapping("/carbon/gas-info")
@Validated
public class CarbonGasInfoController {

    @Resource
    private CarbonGasInfoService gasInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建气体信息")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:create')")
    public CommonResult<Long> createGasInfo(@Valid @RequestBody CarbonGasInfoSaveReqVO createReqVO) {
        Long id = gasInfoService.createGasInfo(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新气体信息")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:update')")
    public CommonResult<Boolean> updateGasInfo(@Valid @RequestBody CarbonGasInfoSaveReqVO updateReqVO) {
        gasInfoService.updateGasInfo(updateReqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/getCode")
    @Operation(summary = "获取编码")
    public CommonResult<String> generateCode() {
        String gasCode = gasInfoService.generateGasCode();
        return CommonResult.success(gasCode);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除气体信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:delete')")
    public CommonResult<Boolean> deleteGasInfo(@RequestParam("id") Long id) {
        gasInfoService.deleteGasInfo(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得气体信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:query')")
    public CommonResult<CarbonGasInfoRespVO> getGasInfo(@RequestParam("id") Long id) {
        return CommonResult.success(gasInfoService.getGasInfo(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得气体信息分页")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:query')")
    public CommonResult<PageResult<CarbonGasInfoRespVO>> getGasInfoPage(@Valid CarbonGasInfoPageReqVO pageReqVO) {
        return CommonResult.success(gasInfoService.getGasInfoPage(pageReqVO));
    }

    @GetMapping("/list")
    @Operation(summary = "获得气体信息列表")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:query')")
    public CommonResult<List<CarbonGasInfoRespVO>> getGasInfoList(@Valid CarbonGasInfoReqVO reqVO) {
        return CommonResult.success(gasInfoService.getGasInfoList(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出气体信息 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportGasInfoExcel(@Valid CarbonGasInfoPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonGasInfoRespVO> result = gasInfoService.getGasInfoListByPage(pageReqVO);
        List<CarbonGasInfoExcelVO> list = CarbonGasInfoConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "气体信息.xls", "数据", CarbonGasInfoExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/气体信息导入模板.xlsx");
        if (!resource.exists()) {
            throw new IllegalStateException("导入模板文件不存在，请联系管理员");
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + resource.getFilename());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        try (InputStream is = resource.getInputStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    @PostMapping("/import")
    @Operation(summary = "导入数据")
    @PreAuthorize("@ss.hasPermission('carbon:gas-info:import')")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<CarbonGasInfoImportVO> list = ExcelUtils.read(file, CarbonGasInfoImportVO.class);
        gasInfoService.importGasInfoList(list);
        return CommonResult.success("导入成功");
    }
}
