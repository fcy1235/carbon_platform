package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 项目管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonProjectPageReqVO extends PageParam {

    @Schema(description = "项目名称", example = "农村改造项目1")
    private String projectName;

    @Schema(description = "项目负责人（联系人编号）", example = "1024")
    private Long contactId;

    @Schema(description = "项目负责人姓名", example = "张三")
    private String contactName;

    @Schema(description = "项目状态", example = "申报中")
    private String projectStatus;

    @Schema(description = "省编码", example = "130000")
    private Long provinceCode;

    @Schema(description = "市编码", example = "130100")
    private Long cityCode;

    @Schema(description = "区/县编码", example = "130102")
    private Long districtCode;

    @Schema(description = "计划开始日期-起", example = "2026-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planStartDateStart;

    @Schema(description = "计划开始日期-止", example = "2026-12-31")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planStartDateEnd;

    @Schema(description = "计划结束日期-起", example = "2026-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planEndDateStart;

    @Schema(description = "计划结束日期-止", example = "2026-12-31")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate planEndDateEnd;

    @Schema(description = "选中的ID列表，用于选中导出", example = "1,2,3")
    private List<Long> ids;
}
