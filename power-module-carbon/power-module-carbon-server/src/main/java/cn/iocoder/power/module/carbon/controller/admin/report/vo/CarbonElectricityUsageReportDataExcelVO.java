package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
public class CarbonElectricityUsageReportDataExcelVO {

    @ExcelProperty("户主姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("电表号")
    private String electricityId;

    @ExcelProperty("采暖季")
    private String heatingSeason;

    @ExcelProperty("采暖季起始时间11.15表底数")
    private BigDecimal startReading;

    @ExcelProperty("采暖季终止时间3.15表底数")
    private BigDecimal endReading;

    @ExcelProperty("采暖季合计用电量（kWh）")
    private BigDecimal totalUsage;

    @ExcelProperty("备注")
    private String remark;

}
