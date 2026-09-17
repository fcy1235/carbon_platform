package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 排放源 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonEmissionSourceExcelVO {

    @ExcelProperty("排放源编码")
    private String sourceCode;

    @ExcelProperty("排放源名称")
    private String sourceName;

    @ExcelProperty("排放范围")
    private String scope;
}
