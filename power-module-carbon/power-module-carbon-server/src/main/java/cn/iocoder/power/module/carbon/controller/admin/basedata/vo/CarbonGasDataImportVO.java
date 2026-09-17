package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 燃气数据导入 VO")
@Data
public class CarbonGasDataImportVO {

    @ExcelProperty("户主姓名")
    @NotBlank(message = "户主姓名不能为空")
    private String username;

    @ExcelProperty("身份证号")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @ExcelProperty("燃气表户号")
    @NotBlank(message = "燃气表户号不能为空")
    private String gasId;

    @ExcelProperty("数据来源")
    private String dataSource;

    @ExcelProperty("最新累计气量(m³)")
    private BigDecimal currentTotal;

    @ExcelProperty("本次用气量(m³)")
    private BigDecimal currentUsage;

    @ExcelProperty("读数时间")
    private LocalDateTime readingTime;
}
