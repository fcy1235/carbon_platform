package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataImportVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataRespVO;

import java.util.List;

public interface CarbonElectricityUsageReportDataService {

    /**
     * 获得电力用电量报表分页
     */
    PageResult<CarbonElectricityUsageReportDataRespVO> getElectricityUsageReportDataPage(CarbonElectricityUsageReportDataPageReqVO pageReqVO);

    /**
     * 获得电力用电量报表列表（用于导出）
     */
    List<CarbonElectricityUsageReportDataRespVO> getElectricityUsageReportDataList(CarbonElectricityUsageReportDataPageReqVO listReqVO);

    /**
     * 导入电力用电量报表数据
     */
    void importElectricityUsageReportDataList(List<CarbonElectricityUsageReportDataImportVO> list);

}
