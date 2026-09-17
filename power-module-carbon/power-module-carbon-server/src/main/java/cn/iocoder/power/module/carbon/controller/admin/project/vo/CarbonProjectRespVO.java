package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 项目管理 Response VO")
@Data
public class CarbonProjectRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PRJ20260101001")
    private String projectCode;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "农村改造项目1")
    private String projectName;

    @Schema(description = "项目负责人（联系人编号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long contactId;

    @Schema(description = "项目负责人姓名", example = "张三")
    private String contactName;

    @Schema(description = "项目负责人手机号", example = "13800138000")
    private String contactPhone;

    @Schema(description = "项目描述", example = "项目描述文本...")
    private String projectDesc;

    @Schema(description = "省编码", example = "130000")
    private Long provinceCode;

    @Schema(description = "市编码", example = "130100")
    private Long cityCode;

    @Schema(description = "区/县编码", example = "130102")
    private Long districtCode;

    @Schema(description = "计划开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    private LocalDate planStartDate;

    @Schema(description = "计划结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06-30")
    private LocalDate planEndDate;

    @Schema(description = "项目周期", example = "0年6月")
    private String projectCycle;

    @Schema(description = "项目状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "申报中")
    private String projectStatus;

    @Schema(description = "总减排量", example = "100")
    private BigDecimal totalReduction;

    @Schema(description = "关联用户列表")
    private List<CarbonProjectUserRespVO> userList;

    @Schema(description = "文档列表")
    private List<CarbonProjectDocRespVO> docs;

    @Schema(description = "进度列表")
    private List<CarbonProjectProgressRespVO> progressList;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
