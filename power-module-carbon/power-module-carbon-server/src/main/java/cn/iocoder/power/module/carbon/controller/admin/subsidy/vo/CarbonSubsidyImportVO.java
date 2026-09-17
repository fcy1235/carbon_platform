package cn.iocoder.power.module.carbon.controller.admin.subsidy.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 补贴管理导入 VO
 */
@Data
public class CarbonSubsidyImportVO {

    @ExcelProperty("户主姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("所属行政区")
    private String division;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("补贴金额")
    private BigDecimal subsidyAmount;
}
