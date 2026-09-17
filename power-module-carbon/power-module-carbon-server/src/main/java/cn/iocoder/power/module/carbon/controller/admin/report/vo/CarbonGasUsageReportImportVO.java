package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 燃气数据报表导入 Excel VO
 *
 * 与「燃气数据报表导入模板.xlsx」的列一一对应（索引映射）：
 * 0-序号（不映射）、1-户主姓名、2-用户编码、3-身份证号、4-燃气表具号、5-采暖季、
 * 6-采暖季起始时间11.15表底数、7-采暖季终止时间3.15表底数、8-采暖季合计用气量（方）、9-备注
 */
@Data
public class CarbonGasUsageReportImportVO {

    @ExcelProperty(index = 1)
    private String username;

    @ExcelProperty(index = 2)
    private String gasUserCode;

    @ExcelProperty(index = 3)
    private String idCard;

    @ExcelProperty(index = 4)
    private String gasId;

    @ExcelProperty(index = 5)
    private String heatingSeason;

    /**
     * 起始表底数。模板中可能是数字或文本，统一按 String 读入后在 Service 层解析
     */
    @ExcelProperty(index = 6)
    private String startReading;

    @ExcelProperty(index = 7)
    private String endReading;

    @ExcelProperty(index = 8)
    private String totalUsage;

    @ExcelProperty(index = 9)
    private String remark;

}
