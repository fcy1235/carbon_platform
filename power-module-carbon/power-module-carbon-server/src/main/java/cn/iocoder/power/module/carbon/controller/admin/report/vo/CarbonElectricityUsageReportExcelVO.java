package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
public class CarbonElectricityUsageReportExcelVO {

    @ExcelProperty("户主姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("电表号")
    private String electricityId;

    @ExcelProperty("采暖季起始时间 表底数")
    private String startReading;

    @ExcelProperty("采暖季结束时间 表底数")
    private String endReading;

    @ExcelProperty("采暖季合计用电量（kWh）")
    private BigDecimal totalUsage;

    @ExcelProperty("备注")
    private String remark;
}
