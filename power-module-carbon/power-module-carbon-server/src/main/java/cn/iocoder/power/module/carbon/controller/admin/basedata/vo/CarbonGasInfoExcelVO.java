package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 气体信息 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonGasInfoExcelVO {

    @ExcelProperty("气体编码")
    private String gasCode;

    @ExcelProperty("气体名称")
    private String gasName;

    @ExcelProperty("GWP值")
    private BigDecimal gwp;

    @ExcelProperty("分类")
    private String category;
}
