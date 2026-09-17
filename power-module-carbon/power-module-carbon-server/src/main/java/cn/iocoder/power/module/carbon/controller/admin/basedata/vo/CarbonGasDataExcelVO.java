package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 燃气数据 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonGasDataExcelVO {

    @ExcelProperty("数据来源")
    private String dataSource;

    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("行政区划")
    private String division;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("燃气户号")
    private String gasId;

    @ExcelProperty("当前累计气量")
    private BigDecimal currentTotal;

    @ExcelProperty("本次用气量")
    private BigDecimal currentUsage;

    @ExcelProperty("上次表读数")
    private BigDecimal lastReading;
}
