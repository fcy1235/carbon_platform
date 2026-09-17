package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.power.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 项目管理创建/修改 Request VO")
@Data
public class CarbonProjectSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "农村改造项目1")
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    @Schema(description = "项目负责人（联系人编号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long contactId;

    @Schema(description = "项目描述", example = "项目描述文本...")
    private String projectDesc;

    @Schema(description = "省编码", example = "130000")
    private Long provinceCode;

    @Schema(description = "市编码", example = "130100")
    private Long cityCode;

    @Schema(description = "区/县编码", example = "130102")
    private Long districtCode;

    @Schema(description = "计划开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    @NotNull(message = "计划开始日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06-30")
    @NotNull(message = "计划结束日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate planEndDate;

    @Schema(description = "项目状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String projectStatus;

    @Schema(description = "关联用户列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private List<CarbonProjectUserSaveReqVO> userList;
}
