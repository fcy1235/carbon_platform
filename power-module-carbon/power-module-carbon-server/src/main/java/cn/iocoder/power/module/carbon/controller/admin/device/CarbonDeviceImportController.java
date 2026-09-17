package cn.iocoder.power.module.carbon.controller.admin.device;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportResultVO;
import cn.iocoder.power.module.carbon.service.CarbonDeviceImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 设备信息导入 Controller
 *
 * 仅提供「下载导入模板」和「导入」两个接口，与已有设备管理（列表/新增/编辑/详情等）解耦。
 */
@Tag(name = "管理后台 - 设备信息导入")
@RestController
@RequestMapping("/carbon/device-import")
@Validated
public class CarbonDeviceImportController {

    @Resource
    private CarbonDeviceImportService deviceImportService;

    @GetMapping("/get-import-template")
    @Operation(summary = "下载设备信息导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("import-template/设备信息导入模板.xlsx");
        if (!resource.exists()) {
            throw new IllegalStateException("导入模板文件不存在，请联系管理员");
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + resource.getFilename());
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        try (InputStream is = resource.getInputStream()) {
            IoUtil.copy(is, response.getOutputStream());
        }
    }

    @PostMapping("/import")
    @Operation(summary = "导入设备信息")
    @PreAuthorize("@ss.hasPermission('carbon:device:import')")
    public CommonResult<CarbonDeviceImportResultVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return CommonResult.success(deviceImportService.importDevice(file));
    }
}
