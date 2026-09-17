package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 排放因子库 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonFactorLibExcelVO {

    @ExcelProperty("排放因子编码")
    private String factorCode;

    @ExcelProperty("排放因子名称")
    private String factorName;

    @ExcelProperty("关联文档")
    private String relatedDoc;

    @ExcelProperty("排放源")
    private String emissionSource;

    @ExcelProperty("单位")
    private String unit;

    @ExcelProperty("因子值")
    private BigDecimal factorValue;

//    @ExcelProperty("关联气体")
//    private String gasList;
}
