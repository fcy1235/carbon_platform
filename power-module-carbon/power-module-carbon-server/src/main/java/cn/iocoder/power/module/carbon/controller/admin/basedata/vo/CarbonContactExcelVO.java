package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 联系人 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonContactExcelVO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("所属公司")
    private String company;

    @ExcelProperty("职位")
    private String position;
}
