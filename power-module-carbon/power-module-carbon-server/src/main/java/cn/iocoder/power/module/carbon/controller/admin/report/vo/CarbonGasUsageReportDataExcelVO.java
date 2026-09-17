package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 燃气数据报表（新表）导出 Excel VO
 */
@Data
public class CarbonGasUsageReportDataExcelVO {

    @ExcelProperty("户主姓名")
    private String username;

    @ExcelProperty("用户编码")
    private String gasUserCode;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("燃气表具号")
    private String gasId;

    @ExcelProperty("采暖季")
    private String heatingSeason;

    @ExcelProperty("采暖季起始表底数")
    private BigDecimal startReading;

    @ExcelProperty("采暖季终止表底数")
    private BigDecimal endReading;

    @ExcelProperty("采暖季合计用气量（m³）")
    private BigDecimal totalUsage;

    @ExcelProperty("备注")
    private String remark;

}
