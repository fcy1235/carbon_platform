package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 设备信息导入 Service 接口
 */
public interface CarbonDeviceImportService {

    /**
     * 导入设备信息
     *
     * @param file 上传的 Excel 文件（仅支持 .xls/.xlsx）
     * @return 导入结果（成功行数、失败行数、逐行错误明细）
     */
    CarbonDeviceImportResultVO importDevice(MultipartFile file) throws IOException;
}
