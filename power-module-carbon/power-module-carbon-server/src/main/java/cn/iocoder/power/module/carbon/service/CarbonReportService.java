package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.*;

import java.util.List;

public interface CarbonReportService {

    List<CarbonGasUsageReportRespVO> getGasUsageReport(CarbonGasUsageReportReqVO reqVO);

    List<CarbonElectricityUsageReportRespVO> getElectricityUsageReport(CarbonElectricityUsageReportReqVO reqVO);

    List<CarbonReformAccountReportRespVO> getReformAccountReport(CarbonReformAccountReportReqVO reqVO);

    /**
     * 获取首页仪表盘统计数据
     */
    CarbonDashboardRespVO getDashboard();

    /**
     * 获取项目碳减排报表
     *
     * @param projectId 项目编号
     */
    CarbonProjectReportRespVO getProjectReport(Long projectId);
}
