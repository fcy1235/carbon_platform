package cn.iocoder.power.module.carbon.controller.admin.project;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonProjectConvert;
import cn.iocoder.power.module.carbon.service.CarbonProjectService;
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

import cn.iocoder.power.framework.apilog.core.enums.OperateTypeEnum;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 项目管理")
@RestController
@RequestMapping("/carbon/project")
@Validated
public class CarbonProjectController {

    @Resource
    private CarbonProjectService projectService;



    @PostMapping("/create")
    @Operation(summary = "创建项目")
    @PreAuthorize("@ss.hasPermission('carbon:project:create')")
    public CommonResult<Long> createProject(@Valid @RequestBody CarbonProjectSaveReqVO createReqVO) {
        Long id = projectService.createProject(createReqVO);
        return CommonResult.success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目")
    @PreAuthorize("@ss.hasPermission('carbon:project:update')")
    public CommonResult<Boolean> updateProject(@Valid @RequestBody CarbonProjectSaveReqVO updateReqVO) {
        projectService.updateProject(updateReqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:project:delete')")
    public CommonResult<Boolean> deleteProject(@RequestParam("id") Long id) {
        projectService.deleteProject(id);
        return CommonResult.success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "变更项目状态")
    @PreAuthorize("@ss.hasPermission('carbon:project:update')")
    public CommonResult<Boolean> updateProjectStatus(@Valid @RequestBody CarbonProjectStatusUpdateReqVO reqVO) {
        projectService.updateProjectStatus(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('carbon:project:query')")
    public CommonResult<CarbonProjectRespVO> getProject(@RequestParam("id") Long id) {
        return CommonResult.success(projectService.getProject(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目分页")
    @PreAuthorize("@ss.hasPermission('carbon:project:query')")
    public CommonResult<PageResult<CarbonProjectRespVO>> getProjectPage(@Valid CarbonProjectPageReqVO pageReqVO) {
        return CommonResult.success(projectService.getProjectPage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得项目精简列表（项目编号+项目名称）")
    @PreAuthorize("@ss.hasPermission('carbon:project:query')")
    public CommonResult<List<CarbonProjectSimpleRespVO>> getProjectSimpleList() {
        return CommonResult.success(projectService.getProjectSimpleList());
    }

    @DeleteMapping("/delete-doc")
    @Operation(summary = "删除项目文档")
    @Parameter(name = "projectId", description = "项目编号", required = true, example = "1024")
    @Parameter(name = "docName", description = "文档名称", required = true, example = "方案.pdf")
    @PreAuthorize("@ss.hasPermission('carbon:project:update')")
    public CommonResult<Boolean> deleteProjectDoc(@RequestParam("projectId") Long projectId,
                                                   @RequestParam("docName") String docName) {
        projectService.deleteProjectDoc(projectId, docName);
        return CommonResult.success(true);
    }

    @PostMapping("/upload-doc")
    @Operation(summary = "上传项目文档")
    @PreAuthorize("@ss.hasPermission('carbon:project:update')")
    public CommonResult<CarbonProjectDocRespVO> uploadProjectDoc(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam(value = "docDesc", required = false) String docDesc) throws Exception {
        return CommonResult.success(projectService.uploadProjectDoc(projectId, file, docDesc));
    }

    @PostMapping("/add-progress")
    @Operation(summary = "添加项目进度记录")
    @PreAuthorize("@ss.hasPermission('carbon:project:update')")
    public CommonResult<Boolean> addProgressRecord(@Valid @RequestBody CarbonProjectProgressSaveReqVO reqVO) {
        projectService.addProgressRecord(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目 Excel")
    @PreAuthorize("@ss.hasPermission('carbon:project:export')")
    @ApiAccessLog(operateType = OperateTypeEnum.EXPORT)
    public void exportProjectExcel(@Valid CarbonProjectPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        List<CarbonProjectRespVO> result = projectService.getProjectList(pageReqVO);
        List<CarbonProjectExcelVO> list = CarbonProjectConvert.INSTANCE.convertExcelList(result);
        ExcelUtils.write(response, "项目管理.xls", "数据", CarbonProjectExcelVO.class, list);
    }


}
