package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import cn.iocoder.power.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 基准线碳排放强度 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonBaselineExcelVO {

    @ExcelProperty("行政区划")
    private String division;

    @ExcelProperty(value = "所属气候子区", converter = DictConvert.class)
    @DictFormat("carbon_climate_zone")
    private String reformType;

    @ExcelProperty("排放强度")
    private BigDecimal intensity;

    @ExcelProperty(value = "单位", converter = DictConvert.class)
    @DictFormat("carbon_unit_measure")
    private String unit;
}
