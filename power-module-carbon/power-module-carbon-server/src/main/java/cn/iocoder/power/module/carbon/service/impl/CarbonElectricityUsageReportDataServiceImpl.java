package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataImportVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataRespVO;
import cn.iocoder.power.module.carbon.convert.CarbonElectricityUsageReportDataConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityUsageReportDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonElectricityUsageReportDataMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.service.CarbonElectricityUsageReportDataService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.ELECTRICITY_USAGE_REPORT_IMPORT_USER_NOT_FOUND;

@Service
@Validated
public class CarbonElectricityUsageReportDataServiceImpl implements CarbonElectricityUsageReportDataService {

    @Resource
    private CarbonElectricityUsageReportDataMapper electricityUsageReportDataMapper;

    @Resource
    private CarbonUserInfoMapper carbonUserInfoMapper;

    @Override
    public PageResult<CarbonElectricityUsageReportDataRespVO> getElectricityUsageReportDataPage(CarbonElectricityUsageReportDataPageReqVO pageReqVO) {
        PageResult<CarbonElectricityUsageReportDataMapper.CarbonElectricityUsageReportDataRespDTO> dtoPage
                = electricityUsageReportDataMapper.selectPageWithUser(pageReqVO);
        if (CollUtil.isEmpty(dtoPage.getList())) {
            return new PageResult<>(Collections.emptyList(), dtoPage.getTotal());
        }
        List<CarbonElectricityUsageReportDataRespVO> voList = CarbonElectricityUsageReportDataConvert.INSTANCE.convertDtoList(dtoPage.getList());
        return new PageResult<>(voList, dtoPage.getTotal());
    }

    @Override
    public List<CarbonElectricityUsageReportDataRespVO> getElectricityUsageReportDataList(CarbonElectricityUsageReportDataPageReqVO listReqVO) {
        List<CarbonElectricityUsageReportDataMapper.CarbonElectricityUsageReportDataRespDTO> dtoList
                = electricityUsageReportDataMapper.selectListWithUser(listReqVO);
        if (CollUtil.isEmpty(dtoList)) {
            return Collections.emptyList();
        }
        return CarbonElectricityUsageReportDataConvert.INSTANCE.convertDtoList(dtoList);
    }

    @Override
    public void importElectricityUsageReportDataList(List<CarbonElectricityUsageReportDataImportVO> list) {
        for (CarbonElectricityUsageReportDataImportVO importVO : list) {
            CarbonUserInfoDO userInfo = carbonUserInfoMapper.selectByIdCardAndElectricityId(importVO.getIdCard(), importVO.getElectricityId());
            if (userInfo == null) {
                throw exception(ELECTRICITY_USAGE_REPORT_IMPORT_USER_NOT_FOUND,
                        "身份证：" + importVO.getIdCard() + "，电表号：" + importVO.getElectricityId());
            }

            // 合计用电量未填写时自动计算（终止表底数 - 起始表底数）
            BigDecimal totalUsage = importVO.getTotalUsage();
            if (totalUsage == null && importVO.getStartReading() != null && importVO.getEndReading() != null) {
                totalUsage = importVO.getEndReading().subtract(importVO.getStartReading());
            }

            CarbonElectricityUsageReportDataDO data = new CarbonElectricityUsageReportDataDO();
            data.setCarbonUserInfoId(userInfo.getId());
            data.setHeatingSeason(importVO.getHeatingSeason());
            data.setStartReading(importVO.getStartReading());
            data.setEndReading(importVO.getEndReading());
            data.setTotalUsage(totalUsage);
            data.setRemark(importVO.getRemark());
            electricityUsageReportDataMapper.insert(data);
        }
    }

}
