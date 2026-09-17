package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 农村气代煤协管员创建/修改 Request VO")
@Data
public class CarbonGasCoordinatorSaveReqVO {

    @Schema(description = "协管员编号", example = "1024")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "性别不能为空")
    private String gender;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED, example = "130102200001010001")
    @NotBlank(message = "身份证号不能为空")
    private String idCardNo;

    @Schema(description = "学历", requiredMode = Schema.RequiredMode.REQUIRED, example = "大专")
    @NotBlank(message = "学历不能为空")
    private String education;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138001")
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @Schema(description = "是否为村两委干部成员 1是 2否", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "是否为村两委干部成员不能为空")
    private String isVillageCommitteeMember;

    @Schema(description = "负责村（社区）区划代码（省,市,区,街道,村 逗号分隔）")
    private String area;

    @Schema(description = "入职培训企业类型", example = "1")
    private String trainingEnterpriseType;

    @Schema(description = "维保企业ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "维保企业不能为空")
    private Long enterpriseId;

    @Schema(description = "入职专业操作技能培训成绩", example = "85.5")
    private BigDecimal trainingScore;

    @Schema(description = "人员状态 1在岗 2离岗", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "人员状态不能为空")
    private String staffStatus;

    @Schema(description = "到岗日期", example = "2024-01-01")
    private LocalDate onDutyDate;

    @Schema(description = "离岗日期", example = "2024-12-31")
    private LocalDate offDutyDate;
}
