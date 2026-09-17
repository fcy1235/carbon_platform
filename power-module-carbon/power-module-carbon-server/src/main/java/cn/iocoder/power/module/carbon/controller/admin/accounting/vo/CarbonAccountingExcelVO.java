package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 碳排放核算 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonAccountingExcelVO {


    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("行政区划")
    private String division;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("改造类型")
    @DictFormat("carbon_transformation_type")
    private String reformType;


    @ExcelProperty("减排量")
    private BigDecimal reduction;

    @ExcelProperty("用电量（kWh）")
    private BigDecimal electricityUsage;

    @ExcelProperty("用气量（m³）")
    private BigDecimal gasUsage;
}
