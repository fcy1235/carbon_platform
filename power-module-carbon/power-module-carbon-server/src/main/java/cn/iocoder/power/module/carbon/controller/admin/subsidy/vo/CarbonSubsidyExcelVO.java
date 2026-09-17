package cn.iocoder.power.module.carbon.controller.admin.subsidy.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 补贴管理 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonSubsidyExcelVO {

    @ExcelProperty("补贴编码")
    private String subsidyCode;

    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("改造类型")
    @DictFormat("carbon_transformation_type")
    private String reformType;

    @ExcelProperty("补贴金额")
    private BigDecimal subsidyAmount;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("发放时间")
    private String grantTime;
}
