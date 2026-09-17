package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 农村气代煤协管员 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonGasCoordinatorExcelVO {

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

    @ExcelProperty("是否为村两委干部成员")
    private String isVillageCommitteeMember;

    @ExcelProperty("负责区域")
    private String areaName;

    @ExcelProperty("入职培训企业类型")
    private String trainingEnterpriseType;

    @ExcelProperty("所属维保企业")
    private String enterpriseName;

    @ExcelProperty("培训成绩")
    private BigDecimal trainingScore;

    @ExcelProperty("人员状态")
    private String staffStatus;

    @ExcelProperty("到岗日期")
    private String onDutyDate;

    @ExcelProperty("离岗日期")
    private String offDutyDate;
}
