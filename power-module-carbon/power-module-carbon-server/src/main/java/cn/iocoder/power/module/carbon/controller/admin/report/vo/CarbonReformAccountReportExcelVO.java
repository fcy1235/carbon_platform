package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
public class CarbonReformAccountReportExcelVO {

    @ExcelProperty("所属区县")
    private String districtName;

    @ExcelProperty("所属乡镇")
    private String townName;

    @ExcelProperty("所属村名称")
    private String villageName;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("户主姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("联系方式")
    private String phone;

    @ExcelProperty("采暖面积（㎡）")
    private BigDecimal heatingArea;

    @ExcelProperty("改造年限")
    private String reformYear;

    @ExcelProperty("使用状态")
    private String useStatus;

    @ExcelProperty("改造类型")
    private String reformType;

    @ExcelProperty("备注")
    private String remark;
}
