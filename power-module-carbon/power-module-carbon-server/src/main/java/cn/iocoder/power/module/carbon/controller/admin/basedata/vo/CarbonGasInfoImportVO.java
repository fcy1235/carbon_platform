package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarbonGasInfoImportVO {

    @ExcelProperty("气体编码")
    private String gasCode;

    @ExcelProperty("气体名称")
    private String gasName;

    @ExcelProperty("GWP值")
    private BigDecimal gwp;

    @ExcelProperty(value = "分类")
    @DictFormat("carbon_gas_category")
    private String category;
}
