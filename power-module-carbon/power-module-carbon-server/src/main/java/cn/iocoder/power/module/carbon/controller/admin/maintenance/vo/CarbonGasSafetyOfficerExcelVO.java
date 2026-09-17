package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 燃气安全员 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonGasSafetyOfficerExcelVO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("身份证号")
    private String idCardNo;

    @ExcelProperty("学历")
    private String education;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("从业资格证编号")
    private String qualificationNo;

    @ExcelProperty("所属企业")
    private String enterpriseName;

    @ExcelProperty("负责区域1")
    private String area1Name;

    @ExcelProperty("负责区域2")
    private String area2Name;

    @ExcelProperty("负责区域3")
    private String area3Name;

    @ExcelProperty("负责区域4")
    private String area4Name;

    @ExcelProperty("负责区域5")
    private String area5Name;

    @ExcelProperty("人员状态")
    private String staffStatus;

    @ExcelProperty("到岗日期")
    private String onDutyDate;

    @ExcelProperty("离岗日期")
    private String offDutyDate;
}
