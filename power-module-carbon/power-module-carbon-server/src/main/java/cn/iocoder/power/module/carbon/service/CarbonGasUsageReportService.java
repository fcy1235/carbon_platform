package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportResultVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataReqVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataRespVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 燃气数据报表（采暖季用气量统计，新表）Service
 */
public interface CarbonGasUsageReportService {

    /**
     * 导入燃气数据报表（Excel），插入 carbon_gas_usage_report 新表
     */
    CarbonDeviceImportResultVO importGasUsageReport(MultipartFile file) throws IOException;

    /**
     * 查询燃气数据报表列表（采暖季 / 用户信息 / 燃气表具号）
     */
    List<CarbonGasUsageReportDataRespVO> getGasUsageDataList(CarbonGasUsageReportDataReqVO reqVO);

}
