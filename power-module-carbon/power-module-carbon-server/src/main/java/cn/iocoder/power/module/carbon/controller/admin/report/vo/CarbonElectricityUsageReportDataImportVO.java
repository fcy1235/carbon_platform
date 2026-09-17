package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 电力用电量报表导入 VO
 *
 * 模板第 1 行为合并标题行、第 2 行为列头，读取时 headRowNumber=2；
 * 模板含「序号」列（索引 0），此处不映射，自动忽略
 */
@Schema(description = "管理后台 - 电力用电量报表导入 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonElectricityUsageReportDataImportVO {

    /** 户主姓名 */
    @ExcelProperty(index = 1)
    private String username;

    /** 身份证号 */
    @ExcelProperty(index = 2)
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    /** 电表号 */
    @ExcelProperty(index = 3)
    @NotBlank(message = "电表号不能为空")
    private String electricityId;

    /** 采暖季（如 2025年） */
    @ExcelProperty(index = 4)
    @NotBlank(message = "采暖季不能为空")
    private String heatingSeason;

    /** 采暖季起始时间11.15表底数 */
    @ExcelProperty(index = 5)
    private BigDecimal startReading;

    /** 采暖季终止时间3.15表底数 */
    @ExcelProperty(index = 6)
    private BigDecimal endReading;

    /** 采暖季合计用电量（kWh） */
    @ExcelProperty(index = 7)
    private BigDecimal totalUsage;

    /** 备注 */
    @ExcelProperty(index = 8)
    private String remark;

}
