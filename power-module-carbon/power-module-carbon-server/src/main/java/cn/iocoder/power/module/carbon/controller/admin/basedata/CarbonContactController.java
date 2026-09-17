package cn.iocoder.power.module.carbon.controller.admin.basedata;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonContactConvert;

import cn.iocoder.power.module.carbon.service.CarbonContactService;
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

@Tag(name = "管理后台 - 联系人")
@RestController
@RequestMapping("/carbon/contact")
@Validated
public class CarbonContactController {

    @Resource
    private CarbonContactService contactService;

    @PostMapping("/create")
    @Operation(summary = "创建联系人")
    @PreAuthorize("@ss.hasPermission('carbon:contact:create')")
    public CommonResult<Long> createContact(@Valid @RequestBody CarbonContactSaveReqVO createReqVO) {
        Long id = contactService.createContact(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新联系人")
    @PreAuthorize("@ss.hasPermission('carbon:contact:update')")
    public CommonResult<Boolean> updateContact(@Valid @RequestBody CarbonContactSaveReqVO updateReqVO) {
        contactService.updateContact(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除联系人")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:contact:delete')")
    public CommonResult<Boolean> deleteContact(@RequestParam("id") Long id) {
        contactService.deleteContact(id);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除联系人")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('carbon:contact:delete')")
    public CommonResult<Boolean> deleteContactList(@RequestParam("ids") List<Long> ids) {
        contactService.deleteContactList(ids);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得联系人")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:contact:query')")
    public CommonResult<CarbonContactRespVO> getContact(@RequestParam("id") Long id) {
        return CommonResult.success(contactService.getContact(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得联系人分页")
    @PreAuthorize("@ss.hasPermission('carbon:contact:query')")
    public CommonResult<PageResult<CarbonContactRespVO>> getContactPage(@Valid CarbonContactPageReqVO pageReqVO) {
        return CommonResult.success(contactService.getContactPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出联系人 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:contact:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportContactExcel(@Valid CarbonContactPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonContactRespVO> result = contactService.getContactList(pageReqVO);
        List<CarbonContactExcelVO> list = CarbonContactConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "联系人.xls", "数据", CarbonContactExcelVO.class, list);
    }
}
