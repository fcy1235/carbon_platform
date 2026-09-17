package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 燃气安全员创建/修改 Request VO")
@Data
public class CarbonGasSafetyOfficerSaveReqVO {

    @Schema(description = "安全员编号", example = "1024")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "性别", example = "男")
    private String gender;

    @Schema(description = "身份证号", example = "130102199001011234")
    private String idCardNo;

    @Schema(description = "学历", example = "本科")
    private String education;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "从业资格证编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ZY2024001")
    @NotBlank(message = "从业资格证编号不能为空")
    private String qualificationNo;

    @Schema(description = "负责区域1（省,市,区,街道,村 逗号分隔的区划代码）", example = "13,1301,130102,130102001,130102001001")
    private String area1;

    @Schema(description = "负责区域2（省,市,区,街道,村 逗号分隔的区划代码）", example = "13,1301,130102,130102002,130102002001")
    private String area2;

    @Schema(description = "负责区域3（省,市,区,街道,村 逗号分隔的区划代码）", example = "13,1301,130103,130103001,130103001001")
    private String area3;

    @Schema(description = "负责区域4（省,市,区,街道,村 逗号分隔的区划代码）")
    private String area4;

    @Schema(description = "负责区域5（省,市,区,街道,村 逗号分隔的区划代码）")
    private String area5;

    @Schema(description = "所属燃气企业（维保企业ID）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "所属燃气企业不能为空")
    private Long enterpriseId;

    @Schema(description = "人员状态 1在岗 2离岗", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "人员状态不能为空")
    private String staffStatus;

    @Schema(description = "到岗日期", example = "2024-01-01")
    private LocalDate onDutyDate;

    @Schema(description = "离岗日期", example = "2024-12-31")
    private LocalDate offDutyDate;
}
