package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonUserInfoConvert;

import cn.iocoder.power.module.carbon.service.CarbonUserInfoService;
import cn.iocoder.power.module.system.api.area.AreaApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
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

@Tag(name = "管理后台 - 用户基本信息")
@RestController
@RequestMapping("/carbon/user-info")
@Validated
public class CarbonUserInfoController {

    @Resource
    private CarbonUserInfoService userInfoService;



    @PostMapping("/create")
    @Operation(summary = "创建用户基本信息")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:create')")
    public CommonResult<Long> createUserInfo(@Valid @RequestBody CarbonUserInfoSaveReqVO createReqVO) {
        Long id = userInfoService.createUserInfo(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户基本信息")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:update')")
    public CommonResult<Boolean> updateUserInfo(@Valid @RequestBody CarbonUserInfoSaveReqVO updateReqVO) {
        userInfoService.updateUserInfo(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户基本信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:delete')")
    public CommonResult<Boolean> deleteUserInfo(@RequestParam("id") Long id) {
        userInfoService.deleteUserInfo(id);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除用户基本信息")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('carbon:user-info:delete')")
    public CommonResult<Boolean> deleteUserInfoList(@RequestParam("ids") Collection<Long> ids) {
        userInfoService.deleteUserInfoList((List<Long>) ids);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户基本信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:query')")
    public CommonResult<CarbonUserInfoRespVO> getUserInfo(@RequestParam("id") Long id) {
        return CommonResult.success(userInfoService.getUserInfo(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户基本信息分页")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:query')")
    public CommonResult<PageResult<CarbonUserInfoRespVO>> getUserInfoPage(@Valid CarbonUserInfoPageReqVO pageReqVO) {
        return CommonResult.success(userInfoService.getUserInfoPage(pageReqVO));
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户基本信息")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:query')")
    public CommonResult<List<CarbonUserInfoRespVO>> getUserInfoList(@Valid CarbonUserInfoListReqVO listReqVO) {
        return CommonResult.success(userInfoService.getUserInfoList(listReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户基本信息 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:user-info:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportUserInfoExcel(@Valid CarbonUserInfoListReqVO listReqVO, HttpServletResponse response) throws IOException {
        List<CarbonUserInfoRespVO> result = userInfoService.getUserInfoList(listReqVO);
        List<CarbonUserInfoExcelVO> list = CarbonUserInfoConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "用户基本信息.xls", "数据", CarbonUserInfoExcelVO.class, list);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/新增用户信息模板.xlsx");
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
    @PreAuthorize("@ss.hasPermission('carbon:user-info:import')")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<CarbonUserInfoImportVO> list = ExcelUtils.read(file, CarbonUserInfoImportVO.class);
        userInfoService.importUserInfoList(list);
        return CommonResult.success("导入成功");
    }
}
