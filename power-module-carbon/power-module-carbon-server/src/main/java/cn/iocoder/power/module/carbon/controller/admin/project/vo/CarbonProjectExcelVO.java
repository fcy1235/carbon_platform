package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 项目管理 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonProjectExcelVO {

    @ExcelProperty("项目编码")
    private String projectCode;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("项目负责人")
    private String contactName;

    @ExcelProperty("项目描述")
    private String projectDesc;

    @ExcelProperty("计划开始日期")
    private String planStartDate;

    @ExcelProperty("计划结束日期")
    private String planEndDate;

    @ExcelProperty("项目周期")
    private String projectCycle;

    @ExcelProperty("项目状态")
    @DictFormat("carbon_project_status")
    private String projectStatus;

    @ExcelProperty("总减排量")
    private BigDecimal totalReduction;
}
